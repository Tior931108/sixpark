package com.example.sixpark.domain.showinfo.controller;

import com.example.sixpark.common.response.ApiResponse;
import com.example.sixpark.domain.showinfo.model.request.ShowInfoSearchRequest;
import com.example.sixpark.domain.showinfo.service.ShowInfoSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ShowInfoSearchTestController {

    private final ShowInfoSearchService showInfoSearchService;

    /**
     * 검색 성능 테스트 전용 api
     */
    @GetMapping("/test/search-showInfo")
    public ResponseEntity<ApiResponse<Map<String, String>>> testCachePerformance() {
        ShowInfoSearchRequest request = new ShowInfoSearchRequest();
        request.setArea("서울");
        PageRequest pageable = PageRequest.of(0, 20);

        Map<String, String> result = new HashMap<>();

        // v1: JPA (캐시 없음)
        long v1Start = System.currentTimeMillis();
        showInfoSearchService.searchShowInfosV1(request, pageable);
        long v1Time = System.currentTimeMillis() - v1Start;
        result.put("v1_jpa_no_cache", v1Time + "ms");

        // v2: 캐시 없음
        long v2Start = System.currentTimeMillis();
        showInfoSearchService.searchShowInfosV2(request, pageable);
        long v2Time = System.currentTimeMillis() - v2Start;
        result.put("v2_no_cache", v2Time + "ms");

        // v3: 첫 호출 (캐시 미스)
        long v3FirstStart = System.currentTimeMillis();
        showInfoSearchService.searchShowInfosV3(request, pageable);
        long v3FirstTime = System.currentTimeMillis() - v3FirstStart;
        result.put("v3_cache_miss", v3FirstTime + "ms");

        // v3: 두 번째 호출 (캐시 히트)
        long v3SecondStart = System.currentTimeMillis();
        showInfoSearchService.searchShowInfosV3(request, pageable);
        long v3SecondTime = System.currentTimeMillis() - v3SecondStart;
        result.put("v3_cache_hit", v3SecondTime + "ms");

        double improvement = ((double)(v2Time - v3SecondTime) / v2Time) * 100;
        result.put("improvement", String.format("%.1f%%", improvement));

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("검색 성능 비교 조회에 성공 했습니다.", result));
    }
}
