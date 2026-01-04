package com.example.sixpark.domain.showinfo.service;

import com.example.sixpark.domain.genre.entity.Genre;
import com.example.sixpark.domain.genre.service.GenreService;
import com.example.sixpark.domain.showinfo.entity.ShowInfo;
import com.example.sixpark.domain.showinfo.model.dto.KopisShowInfoDto;
import com.example.sixpark.domain.showinfo.repository.ShowInfoRepository;
import com.example.sixpark.domain.showtime.entity.ShowTime;
import com.example.sixpark.domain.showtime.model.dto.KopisShowDetailDto;
import com.example.sixpark.domain.showtime.repository.ShowTimeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final ShowTimeRepository showTimeRepository;
    private final GenreService genreService;
    private final KopisApiService kopisApiService;

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
                log.warn("조회된 공연이 없습니다.");
                return;
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
                        log.info("→ 이미 존재하는 공연, 건너뜀");
                        skipCount++;
                        continue;
                    }

                    // 상세 정보 조회
                    KopisShowDetailDto detailDto =
                            kopisApiService.fetchShowTimeDetail(dto.getMt20id());

                    if (detailDto == null) {
                        log.warn("→ 상세 정보 조회 실패, 건너뜀");
                        errorCount++;
                        continue;
                    }

                    // 장르 생성 또는 조회
                    Genre genre = genreService.getOrCreateGenre(dto.getGenrenm());

                    // ShowInfo 생성 및 저장
                    ShowInfo showInfo = convertToShowInfo(dto, detailDto, genre);
                    ShowInfo savedShowInfo = showInfoRepository.save(showInfo);
                    log.info("→ ShowInfo 저장 완료 (ID: {})", savedShowInfo.getId());

                    // ShowTime 생성 및 저장 (1개만)
                    ShowTime showTime = createShowTime(detailDto, dto, savedShowInfo);
                    if (showTime != null) {
                        showTimeRepository.save(showTime);
                        log.info("→ ShowTime 저장 완료");
                    } else {
                        log.warn("→ ShowTime 정보가 없습니다.");
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

//    /**
//     * KOPIS DTO -> ShowTime Entity 리스트 변환 (공연 시간 파싱으로 인한 중복 현상 제거)
//     */
//    private List<ShowTime> convertToShowTimes(KopisShowDetailDto detailDto,
//                                              KopisShowInfoDto dto,
//                                              ShowInfo showInfo) {
//        List<ShowTime> showTimes = new ArrayList<>();
//
//        if (detailDto.getDtguidance() == null || detailDto.getDtguidance().isEmpty()) {
//            return showTimes;
//        }
//
//        try {
//            // "화요일 ~ 일요일(13:00, 16:00, 19:00)" 형식 파싱
//            Pattern pattern = Pattern.compile("(\\d{2}):(\\d{2})");
//            Matcher matcher = pattern.matcher(detailDto.getDtguidance());
//
//            Set<LocalTime> uniqueTimes = new HashSet<>();
//
//            while (matcher.find()) {
//                int hour = Integer.parseInt(matcher.group(1));
//                int minute = Integer.parseInt(matcher.group(2));
//                LocalTime time = LocalTime.of(hour, minute);
//
//                // 중복 시간 제거
//                if (uniqueTimes.add(time)) {
//                    ShowTime showTime = ShowTime.create(
//                            showInfo,
//                            dto.getArea() != null ? dto.getArea() : "미정",
//                            dto.getFcltynm() != null ? dto.getFcltynm() : "미정",
//                            500L,  // 좌석 규모 : 500 고정
//                            detailDto.getDtguidance(),
//                            detailDto.getPrfruntime()
//                    );
//                    showTimes.add(showTime);
//                }
//            }
//
//        } catch (Exception e) {
//            log.warn("공연 시간 파싱 실패: {}", detailDto.getDtguidance());
//        }
//
//        return showTimes;
//    }

//    /**
//     * KOPIS DTO -> ShowTime Entity 리스트 변환 (공연 시간 파싱으로 인한 중복 현상 제거)
//     * (,) 요일별 분리
//     */
//    private List<ShowTime> parseAndCreateShowTimes(KopisShowDetailDto detailDto,
//                                                   KopisShowInfoDto dto,
//                                                   ShowInfo showInfo) {
//        List<ShowTime> showTimes = new ArrayList<>();
//
//        // dtguidance 정보 확인
//        if (detailDto.getDtguidance() == null || detailDto.getDtguidance().isEmpty()) {
//            log.debug("→ dtguidance 정보 없음");
//            return showTimes;
//        }
//
//        try {
//            // 콤마로 split하여 요일 그룹별로 처리
//            String[] timeGroups = splitByComma(detailDto.getDtguidance());
//
//            // prfruntime 기본값 설정
//            String prfruntime = (detailDto.getPrfruntime() != null && !detailDto.getPrfruntime().isEmpty())
//                    ? detailDto.getPrfruntime()
//                    : "정보 없음";
//
//            for (String timeGroup : timeGroups) {
//                timeGroup = timeGroup.trim();
//
//                if (timeGroup.isEmpty()) {
//                    continue;
//                }
//
//                ShowTime showTime = ShowTime.create(
//                        showInfo,
//                        dto.getArea() != null ? dto.getArea() : "미정",
//                        dto.getFcltynm() != null ? dto.getFcltynm() : "미정",
//                        500L,  // 좌석 수 : 500 고정
//                        timeGroup,  // 공연 시간 정보 (예: 수요일 ~ 금요일(19:30))
//                        prfruntime  // 공연 총시간 (예: 1시간 30분)
//                );
//
//                showTimes.add(showTime);
//                log.debug("→ ShowTime 생성: {}", timeGroup);
//            }
//
//            log.info("→ 파싱된 공연 시간 그룹: {} 건", showTimes.size());
//
//        } catch (Exception e) {
//            log.warn("→ 공연 시간 파싱 실패: {}", detailDto.getDtguidance(), e);
//        }
//
//        return showTimes;
//    }

//    /**
//     * 콤마로 문자열 분리 (괄호 안의 콤마는 무시)
//     * 예: "수요일(19:30), 토요일(16:00,19:00), 일요일(15:00)"
//     *     → ["수요일(19:30)", "토요일(16:00,19:00)", "일요일(15:00)"]
//     */
//    private String[] splitByComma(String dtguidance) {
//        List<String> result = new ArrayList<>();
//        StringBuilder current = new StringBuilder();
//        int depth = 0;  // 괄호 깊이
//
//        for (char c : dtguidance.toCharArray()) {
//            if (c == '(') {
//                depth++;
//                current.append(c);
//            } else if (c == ')') {
//                depth--;
//                current.append(c);
//            } else if (c == ',' && depth == 0) {
//                // 괄호 밖의 콤마만 구분자로 사용
//                if (current.length() > 0) {
//                    result.add(current.toString().trim());
//                    current = new StringBuilder();
//                }
//            } else {
//                current.append(c);
//            }
//        }
//
//        // 마지막 그룹 추가
//        if (current.length() > 0) {
//            result.add(current.toString().trim());
//        }
//
//        return result.toArray(new String[0]);
//    }

    /**
     * ShowTime 생성 (파싱 없이 원본 그대로 저장)
     */
    private ShowTime createShowTime(KopisShowDetailDto detailDto,
                                    KopisShowInfoDto dto,
                                    ShowInfo showInfo) {
        // dtguidance 공연 시간 확인
        if (detailDto.getDtguidance() == null || detailDto.getDtguidance().isEmpty()) {
            log.debug("→ dtguidance 정보 없음");
            return null;
        }

        // prfruntime 기본값 설정
        String prfruntime = (detailDto.getPrfruntime() != null && !detailDto.getPrfruntime().isEmpty())
                ? detailDto.getPrfruntime()
                : "정보 없음";

        // ShowTime 생성 (원본 그대로 저장)
        ShowTime showTime = ShowTime.create(
                showInfo,
                dto.getArea() != null ? dto.getArea() : "미정",
                dto.getFcltynm() != null ? dto.getFcltynm() : "미정",
                500L,  // 좌석 수 : 500 고정
                detailDto.getDtguidance(),  // 원본 시간 그대로 저장
                prfruntime
        );

        log.debug("→ ShowTime 생성: dtguidance={}, prfruntime={}",
                detailDto.getDtguidance(), prfruntime);

        return showTime;
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
}
