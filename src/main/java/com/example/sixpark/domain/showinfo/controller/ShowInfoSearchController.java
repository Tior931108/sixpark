package com.example.sixpark.domain.showinfo.controller;

import com.example.sixpark.common.response.PageResponse;
import com.example.sixpark.domain.showinfo.model.request.ShowInfoSearchRequest;
import com.example.sixpark.domain.showinfo.model.response.ShowInfoSearchResponse;
import com.example.sixpark.domain.showinfo.service.ShowInfoSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ShowInfoSearchController {

    private final ShowInfoSearchService showInfoSearchService;

    /**
     * v1: 공연 검색 (JPA 기본)
     */
    @GetMapping("/showInfoes/v1/search")
    public ResponseEntity<PageResponse<ShowInfoSearchResponse>> searchV1(@RequestBody ShowInfoSearchRequest request, @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ShowInfoSearchResponse> resultPage = showInfoSearchService.searchShowInfosV1(request, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(PageResponse.success("공연 검색이 완료 되었습니다.", resultPage));
    }

    /**
     * v2: 공연 검색 (QueryDSL + 동적 쿼리 + 인덱스)
     */
    @GetMapping("/showInfoes/v2/search")
    public ResponseEntity<PageResponse<ShowInfoSearchResponse>> searchV2(@RequestBody ShowInfoSearchRequest request, @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ShowInfoSearchResponse> resultPage = showInfoSearchService.searchShowInfosV2(request, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(PageResponse.success("공연 검색이 완료 되었습니다.", resultPage));
    }

    /**
     * v3: 공연 검색 QueryDSL + 인덱스 + Local Cache
     */
    @GetMapping("/showInfoes/v3/search")
    public ResponseEntity<PageResponse<ShowInfoSearchResponse>> searchV3(@RequestBody ShowInfoSearchRequest request, @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC)
    Pageable pageable) {

        Page<ShowInfoSearchResponse> resultPage = showInfoSearchService.searchShowInfosV3(request, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(PageResponse.success("공연 검색이 완료 되었습니다.", resultPage));
    }
}
