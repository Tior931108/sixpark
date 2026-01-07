package com.example.sixpark.domain.showinfo.service;

import com.example.sixpark.common.enums.ErrorMessage;
import com.example.sixpark.common.excepion.CustomException;
import com.example.sixpark.domain.genre.entity.Genre;
import com.example.sixpark.domain.genre.service.GenreService;
import com.example.sixpark.domain.showinfo.entity.ShowInfo;
import com.example.sixpark.domain.showinfo.model.dto.KopisShowInfoDto;
import com.example.sixpark.domain.showinfo.model.dto.ShowInfoDto;
import com.example.sixpark.domain.showinfo.model.request.ShowInfoUpdateRequest;
import com.example.sixpark.domain.showinfo.model.response.ShowInfoDetailResponse;
import com.example.sixpark.domain.showinfo.model.response.ShowInfoResponse;
import com.example.sixpark.domain.showinfo.repository.ShowInfoRepository;
import com.example.sixpark.domain.showplace.entity.ShowPlace;
import com.example.sixpark.domain.showplace.model.dto.KopisShowDetailDto;
import com.example.sixpark.domain.showplace.model.dto.ShowPlaceDto;
import com.example.sixpark.domain.showplace.repository.ShowPlaceRepository;
import com.example.sixpark.domain.showplace.service.ShowPlaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShowInfoService {

    private final ShowInfoRepository showInfoRepository;
    private final ShowPlaceRepository showPlaceRepository;
    private final ShowPlaceService showPlaceService;
    private final GenreService genreService;
    private final KopisApiService kopisApiService;
    private final ViewCountService viewCountService;

    /**
     * 공연 생성 (KOPIS API 요청)
     * - 장르 테이블 + 공연 정보 테이블 + 공연 시간 테이블 DATA 삽입
     */
    @Transactional
    public void createShowInfoFromKopis(String startDate, String endDate, Integer cpage, Integer rows) {
        try {
            log.info(" 공연 정보 조회 기간: {} ~ {}", startDate, endDate);
            log.info("조회 설정: cpage={} , rows={} ", cpage, rows);

            // KOPIS API에서 공연 목록 조회
            List<KopisShowInfoDto> performances =
                    kopisApiService.fetchShowInfoList(startDate, endDate, cpage, rows);

            if (performances.isEmpty()) {
                throw new CustomException(ErrorMessage.NOT_FOUND_SHOWINFO);
            }

            // 로깅 전용 변수
            int totalCount = performances.size();
            int successCount = 0;
            int skipCount = 0;
            int errorCount = 0;

            for (int i = 0; i < performances.size(); i++) {
                KopisShowInfoDto dto = performances.get(i);

                try {
                    log.info("[{}/{}] 처리 중: {} ({})",
                            i + 1, totalCount, dto.getPrfnm(), dto.getMt20id());

                    // 중복 체크
                    if (showInfoRepository.existsByMt20id(dto.getMt20id())) {
                        skipCount++;
                        continue;
                    }

                    // 상세 정보 조회
                    KopisShowDetailDto detailDto =
                            kopisApiService.fetchShowTimeDetail(dto.getMt20id());

                    // 상세 정보 실패
                    if (detailDto == null) {
                        errorCount++;
                        continue;
                    }

                    // 장르 생성 또는 조회
                    Genre genre = genreService.getOrCreateGenre(dto.getGenrenm());

                    // ShowInfo 생성 및 저장
                    ShowInfo showInfo = convertToShowInfo(dto, detailDto, genre);
                    ShowInfo savedShowInfo = showInfoRepository.save(showInfo);
                    log.info("→ ShowInfo 저장 완료 (ID: {})", savedShowInfo.getId());

                    // ShowPlace 생성 및 저장 (1개만)
                    ShowPlace showPlace = createShowPlace(detailDto, dto, savedShowInfo);
                    if (showPlace != null) {
                        showPlaceRepository.save(showPlace);
                        log.info("→ ShowPlace 저장 완료");
                    } else {
                        log.warn("→ ShowPlace 정보가 없습니다.");
                    }

                    successCount++;

                    // API 호출 제한 고려 (100ms 대기)
                    Thread.sleep(100);

                } catch (Exception e) {
                    log.error("→ 공연 저장 중 오류 발생: {}", dto.getMt20id(), e);
                    errorCount++;
                }
            }

            log.info("=== 공연 정보 수집 완료 ===");
            log.info("전체: {}건, 성공: {}건, 중복: {}건, 실패: {}건",
                    totalCount, successCount, skipCount, errorCount);

        } catch (Exception e) {
            log.error("KOPIS API 공연 생성 실패", e);
            throw new RuntimeException("공연 생성 중 오류 발생", e);
        }
    }

    /**
     * KOPIS DTO -> ShowInfo Entity 변환
     */
    private ShowInfo convertToShowInfo(KopisShowInfoDto dto,
                                       KopisShowDetailDto detailDto,
                                       Genre genre) {
        // 날짜 파싱
        LocalDate startDate = parseDate(dto.getPrfpdfrom());
        LocalDate endDate = parseDate(dto.getPrfpdto());

        // 가격 파싱
        Integer price = parsePrice(detailDto.getPcseguidance());
        if (price == null) {
            price = 0;  // NOT NULL 제약조건
        }

        // 출연진 처리 (VARCHAR(500) 제약)
        String prfcast = "";
        if (detailDto.getPrfcast() != null && !detailDto.getPrfcast().isEmpty()) {
            prfcast = detailDto.getPrfcast().length() > 500
                    ? detailDto.getPrfcast().substring(0, 500)
                    : detailDto.getPrfcast();
        }

        // ShowInfo 생성
        return ShowInfo.create(
                genre,
                dto.getMt20id(),
                dto.getPrfnm(),
                prfcast,
                startDate,
                endDate,
                dto.getPoster(),
                price
        );
    }


    /**
     * ShowPlace 생성 (파싱 없이 원본 그대로 저장)
     */
    private ShowPlace createShowPlace(KopisShowDetailDto detailDto,
                                     KopisShowInfoDto dto,
                                     ShowInfo showInfo) {
        // dtguidance 공연 시간 확인
        if (detailDto.getDtguidance() == null || detailDto.getDtguidance().isEmpty()) {
            log.debug("→ dtguidance 정보 없음");
            return null;
        }

        // prfruntime 기본값 설정
        String prfruntime = (detailDto.getPrfruntime() != null && !detailDto.getPrfruntime().isEmpty()) ? detailDto.getPrfruntime() : "정보 없음";

        // ShowTime 생성 (원본 그대로 저장)
        ShowPlace showPlace = ShowPlace.create(
                showInfo,
                dto.getArea() != null ? dto.getArea() : "미정",
                dto.getFcltynm() != null ? dto.getFcltynm() : "미정",
                500L,  // 좌석 수 : 500 고정
                detailDto.getDtguidance(),  // 원본 시간 그대로 저장
                prfruntime
        );

        log.debug("→ ShowTime 생성: dtguidance={}, prfruntime={}",
                detailDto.getDtguidance(), prfruntime);

        return showPlace;
    }

    /**
     * 날짜 파싱 (yyyy.MM.dd 형식)
     */
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
            return LocalDate.parse(dateStr, formatter);
        } catch (Exception e) {
            log.warn("날짜 파싱 실패: {}", dateStr);
            return null;
        }
    }

    /**
     * 가격 파싱 - 좌석 등급제가 있는 경우 가장 높은 금액만 추출
     * 예: "R석 50,000원, S석 30,000원, A석 20,000원" -> 50000
     */
    private Integer parsePrice(String priceStr) {
        if (priceStr == null || priceStr.isEmpty()) {
            return null;
        }

        try {
            // 모든 숫자(콤마 포함) 추출
            Pattern pattern = Pattern.compile("\\d[\\d,]+");
            Matcher matcher = pattern.matcher(priceStr);

            Integer maxPrice = 0;

            // 모든 금액을 찾아서 최대값 추출
            while (matcher.find()) {
                String numStr = matcher.group().replace(",", "");
                Integer price = Integer.parseInt(numStr);

                if (maxPrice == 0 || price > maxPrice) {
                    maxPrice = price;
                }
            }

            if (maxPrice != 0) {
                log.debug("가격 파싱 완료: {} -> {}", priceStr, maxPrice);
            }

            return maxPrice;

        } catch (Exception e) {
            log.warn("가격 파싱 실패: {}", priceStr);
        }

        // 무료 공연이거나, 가격정보가 없는 경우 0원
        return 0;
    }


    /**
     * 공연 전체 조회 (페이징)
     *
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @param sortBy 정렬 기준 (기본값: id)
     * @param direction 정렬 방향 (ASC, DESC)
     * @return 페이징된 공연 목록
     */
    @Transactional(readOnly = true)
    public Page<ShowInfoResponse> getAllShowInfos(Long genreId, int page, int size, String sortBy, String direction) {

        // 장르 존재 확인 : 예외처리 장르 service에서 진행
        Genre genre = genreService.getGenreById(genreId);

        // 정렬 방향 설정
        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;

        // Pageable 생성
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        // 장르별 페이징 조회 (N+1 문제 해결된 메서드 사용)
        Page<ShowInfo> showInfoPage = showInfoRepository.findByGenreIdWithGenre(genreId, pageable);

        // 해당 장르의 공연이 없는 경우,
       if(showInfoPage == null){
           throw new CustomException(ErrorMessage.NOT_FOUND_SHOWINFO);
       }

        // Entity -> DTO 변환 (ShowInfoResponse의 from 메서드 사용)
        Page<ShowInfoResponse> responsePage = showInfoPage.map(showInfo ->
                ShowInfoResponse.from(ShowInfoDto.from(showInfo))
        );

        return responsePage;
    }

    /**
     * 공연 상세 조회 (1개)
     *
     * @param showInfoId 공연 ID
     * @return 공연 상세 정보 (ShowInfo + Genre + ShowTime)
     */
    @Transactional(readOnly = true)
    public ShowInfoDetailResponse getShowInfoDetail(Long showInfoId) {
        // 장르 기반 공연 정보 조회 (Genre Fetch Join)
        ShowInfo showInfo = getShowInfoAndGenreById(showInfoId);

        // 공연 시간및 장소 정보 조회 (1:1 관계) - 예외처리 포함
        ShowPlace showPlace = showPlaceService.getShowPlaceById(showInfoId);

        // 상세 조회시 조회수 증가 - 일간/주간 모두포함
        viewCountService.incrementDailyView(showInfo.getGenre().getId(), showInfoId);
        viewCountService.incrementWeeklyView(showInfo.getGenre().getId(), showInfoId);

        return ShowInfoDetailResponse.from(ShowInfoDto.from(showInfo), ShowPlaceDto.from(showPlace));
    }

    /**
     * 공연 정보 수정 (관리자 전용)
     */
    @Transactional
    public ShowInfoDetailResponse updateShowInfo(Long showInfoId, ShowInfoUpdateRequest request) {
        // 공연 정보 조회 (상세 정보 포함)
        ShowInfo showInfo = getShowInfoAndDetailsById(showInfoId);

        // 공연 시간및 장소 정보 조회 (1:1 관계) - 예외처리 포함
        ShowPlace showPlace = showPlaceService.getShowPlaceById(showInfoId);

        // 공연 정보 수정 - Optinal 부분적 업데이트
        showInfo.updatePartial(request);

        return ShowInfoDetailResponse.from(ShowInfoDto.from(showInfo), ShowPlaceDto.from(showPlace));
    }

    /**
     * 공연 삭제 (관리자 전용 - 논리 삭제)
     */
    @Transactional
    public void deleteShowInfo(Long showInfoId) {
        // 공연 정보 조화
        ShowInfo showInfo = getShowInfoAndDetailsById(showInfoId);

        // 논리 삭제 (ShowPlace도 함께)
        showInfo.softDelete();
    }

    /**
     * Genre 기반 ShowInfo ID 조회 (예외 처리 포함)
     */
    public ShowInfo getShowInfoAndGenreById(Long showInfoId) {
        return showInfoRepository.findByIdWithGenre(showInfoId)
                .orElseThrow(() -> new CustomException(ErrorMessage.NOT_FOUND_SHOWINFO));
    }

    /**
     * ShowInfo ID 기반 공연 상세 조회 (예외 처리 포함)
     */
    public ShowInfo getShowInfoAndDetailsById(Long showInfoId) {
        return showInfoRepository.findByIdWithDetails(showInfoId)
                .orElseThrow(() -> new CustomException(ErrorMessage.NOT_FOUND_SHOWINFO));
    }
}
