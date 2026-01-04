package com.example.sixpark.domain.showinfo.controller;

import com.example.sixpark.common.response.ApiResponse;
import com.example.sixpark.domain.showinfo.service.ShowInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class ShowInfoController {

    private final ShowInfoService showInfoService;

    /**
     * 공연 생성 (KOPIS API 요청)
     *
     * API 호출시에 필수 파라미터:
     * - stdate: 공연시작일 (YYYYMMDD, 8자리) ex) 20260101
     * - eddate: 공연종료일 (YYYYMMDD, 8자리)
     * - cpage: 현재 페이지 (기본값 1)
     * - rows: 페이지당 목록 수 (기본값 100, 최대 100건)
     *
     * 응답 response는 구현한 3개 테이블 (장르 / 공연정보 / 공연시간) 에
     * API 데이터가 들어가는것이기에
     * API 요청 및 저장 완료하는 결과 문자열만 도출함.
     */
    @PostMapping("/admin/showInfoes")
    public ResponseEntity<ApiResponse<String>> showInfoAndShowTimeAndGenreCreate(
            @RequestParam(name = "stdate") String stdate,
            @RequestParam(name = "eddate") String eddate,
            @RequestParam(name = "cpage", defaultValue = "1") Integer cpage,
            @RequestParam(name = "rows", defaultValue = "100") Integer rows) {

        showInfoService.createShowInfoFromKopis(stdate, eddate, cpage, rows);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("공연 정보가 생성 되었습니다.", "KOPIS API 공연 저장 완료"));

    }
}
