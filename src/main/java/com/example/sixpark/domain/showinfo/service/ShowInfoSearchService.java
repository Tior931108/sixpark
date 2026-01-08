package com.example.sixpark.domain.showinfo.service;

import com.example.sixpark.common.enums.ErrorMessage;
import com.example.sixpark.common.excepion.CustomException;
import com.example.sixpark.domain.showinfo.entity.ShowInfo;
import com.example.sixpark.domain.showinfo.model.request.ShowInfoSearchRequest;
import com.example.sixpark.domain.showinfo.model.response.ShowInfoSearchResponse;
import com.example.sixpark.domain.showinfo.repository.ShowInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShowInfoSearchService {

    private final ShowInfoRepository showInfoRepository;

    /**
     * v1: JPA 기본 검색
     */
    @Transactional(readOnly = true)
    public Page<ShowInfoSearchResponse> searchShowInfosV1(ShowInfoSearchRequest request, Pageable pageable) {

        log.info("v1 검색 시작: {}", request);

        Page<ShowInfo> showInfoPage;

        // 검색 조건이 없으면 전체 조회
        if (!request.hasSearchCondition()) {
            log.info("검색 조건 없음 - 전체 조회");
            showInfoPage = showInfoRepository.findAllActiveWithDetails(pageable);
        } else {
            // 검색 조건이 있으면 검색
            log.info("검색 조건: prfnm={}, prfcast={}, area={}, fcltynm={}, dtguidance={}", request.getPrfnm(), request.getPrfcast(), request.getArea(), request.getFcltynm(), request.getDtguidance());

            showInfoPage = showInfoRepository.searchShowInfosV1(request.getPrfnm(), request.getPrfcast(), request.getArea(), request.getFcltynm(), request.getDtguidance(), pageable);
        }

        log.info("v1 검색 완료: {} 건", showInfoPage.getTotalElements());

        // 예외처리 공통 메소드
        notFoundSearchList(showInfoPage);

        // 결과값 페이지 반환
        return showInfoPage.map(ShowInfoSearchResponse::new);
    }

    /**
     * v2: QueryDSL 동적 쿼리 검색
     */
    @Transactional(readOnly = true)
    public Page<ShowInfoSearchResponse> searchShowInfosV2(
            ShowInfoSearchRequest request,
            Pageable pageable) {

        log.info("v2 검색 시작: {}", request);

        Page<ShowInfo> showInfoPage = showInfoRepository.searchShowInfosV2(request, pageable);

        log.info("v2 검색 완료: {} 건", showInfoPage.getTotalElements());

        // 예외처리 공통 메소드
        notFoundSearchList(showInfoPage);

        return showInfoPage.map(ShowInfoSearchResponse::new);
    }

    /**
     * v3: QueryDSL + Local Cache (Caffeine)
     */
    @Cacheable(
            value = "showInfoSearch",
            key = "'search_' + #request.prfnm + '_' + #request.area + '_' + #pageable.pageNumber",
            unless = "#result == null || !#result.hasContent()"
    )
    public Page<ShowInfoSearchResponse> searchShowInfosV3(ShowInfoSearchRequest request, Pageable pageable) {

        log.info("v3 검색 시작 (Local Cache): {}", request);

        // v2와 동일한 로직 (QueryDSL)
        Page<ShowInfo> showInfoPage = showInfoRepository.searchShowInfosV2(request, pageable);

        log.info("v3 검색 완료: {} 건", showInfoPage.getTotalElements());

        // 예외처리 공통 메소드
        notFoundSearchList(showInfoPage);

        return showInfoPage.map(ShowInfoSearchResponse::new);

    }

    // 공통 예외 처리
    private void notFoundSearchList(Page<ShowInfo> showInfoPage) {
        if (showInfoPage.getTotalElements() == 0) {
            throw new CustomException(ErrorMessage.NOT_FOUND_SEARCH_LIST);
        }
    }
}
