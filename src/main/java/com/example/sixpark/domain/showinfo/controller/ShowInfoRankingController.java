package com.example.sixpark.domain.showinfo.controller;

import com.example.sixpark.common.response.ApiResponse;
import com.example.sixpark.domain.showinfo.model.response.ShowInfoRankingResponse;
import com.example.sixpark.domain.showinfo.service.ShowInfoRankingService;
import com.example.sixpark.domain.showinfo.service.ViewCountSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ShowInfoRankingController {

    private final ShowInfoRankingService showInfoRankingService;
    private final ViewCountSyncService viewCountSyncService;

    /**
     * 장르별 공연 일간 TOP10 조회 (조회수 기준)
     */
    @GetMapping("/genre/{genreId}/showInfoes/ranking/daily")
    public ResponseEntity<ApiResponse<List<ShowInfoRankingResponse>>> getDailyTop10(@PathVariable Long genreId) {

        List<ShowInfoRankingResponse> rankings = showInfoRankingService.getDailyTop10(genreId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("장르별 공연 일간 TOP10 조회가 완료되었습니다.", rankings));
    }

    /**
     * 장르별 공연 주간 TOP10 조회 (조회수 기준)
     */
    @GetMapping("/genre/{genreId}/showInfoes/ranking/weekly")
    public ResponseEntity<ApiResponse<List<ShowInfoRankingResponse>>> getWeeklyTop10(@PathVariable Long genreId) {

        List<ShowInfoRankingResponse> rankings = showInfoRankingService.getWeeklyTop10(genreId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("장르별 공연 주간 TOP10 조회가 완료되었습니다.", rankings));
    }

    /**
     * 테스트용: 장르별 랜덤 조회수 생성
     */
    @PostMapping("/genre/{genreId}/showInfoes/test/ranking-views")
    public ResponseEntity<ApiResponse<Void>> generateRandomViews(@PathVariable Long genreId) {

        showInfoRankingService.generateRandomViewsForGenre(genreId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("랜덤 조회수 생성 완료 되었습니다."));
    }

    /**
     * 테스트용 : 조회수 수동 동기화 API
     */
    @PostMapping("/sync/manual")
    public ResponseEntity<ApiResponse<Void>> manualSync() {

        viewCountSyncService.manualSync();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("조회수 수동 동기화 완료 했습니다."));
    }
}
