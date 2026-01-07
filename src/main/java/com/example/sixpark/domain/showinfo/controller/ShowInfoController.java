package com.example.sixpark.domain.showinfo.controller;

import com.example.sixpark.common.response.ApiResponse;
import com.example.sixpark.common.response.PageResponse;
import com.example.sixpark.domain.showinfo.model.request.ShowInfoUpdateRequest;
import com.example.sixpark.domain.showinfo.model.response.ShowInfoDetailResponse;
import com.example.sixpark.domain.showinfo.model.response.ShowInfoResponse;
import com.example.sixpark.domain.showinfo.service.ShowInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class ShowInfoController {

    private final ShowInfoService showInfoService;

    /**
     * 공연 생성 (KOPIS API 요청) - (관리자 전용)
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
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/showInfoes")
    public ResponseEntity<ApiResponse<String>> createShowInfoAndShowTimeAndGenre(@RequestParam(name = "stdate") String stdate, @RequestParam(name = "eddate") String eddate, @RequestParam(name = "cpage", defaultValue = "1") Integer cpage, @RequestParam(name = "rows", defaultValue = "100") Integer rows) {

        showInfoService.createShowInfoFromKopis(stdate, eddate, cpage, rows);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("공연 정보가 생성 되었습니다.", "KOPIS API 공연 저장 완료"));

    }


    /**
     * 장르별 공연 전체 조회 (페이징)
     *
     * @param page 페이지 번호 (0부터 시작, 기본값: 0)
     * @param size 페이지 크기 (기본값: 10)
     * @param sortBy 정렬 기준 (기본값: id)
     * @param direction 정렬 방향 (ASC, DESC, 기본값: DESC)
     * @return 페이징된 공연 목록
     */
    @GetMapping("/genre/{genreId}/showInfoes")
    public ResponseEntity<PageResponse<ShowInfoResponse>> getAllShowInfos(@PathVariable Long genreId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "DESC") String direction) {

        Page<ShowInfoResponse> infoResponses = showInfoService.getAllShowInfos(genreId, page, size, sortBy, direction);

        return ResponseEntity.status(HttpStatus.OK).body(PageResponse.success("장르별 공연 전체 조회를 완료했습니다.", infoResponses));
    }


    /**
     * 공연 상세 조회 (1개)
     *
     * @param showInfoId 공연 ID
     * @return 공연 상세 정보 (ShowInfo + Genre + ShowTime)
     */
    @GetMapping("/showInfoes/{showInfoId}")
    public ResponseEntity<ApiResponse<ShowInfoDetailResponse>> getShowInfoDetail(@PathVariable Long showInfoId) {

        ShowInfoDetailResponse response = showInfoService.getShowInfoDetail(showInfoId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("공연 상세 조회를 완료했습니다.", response));

    }

    /**
     * 공연 정보 수정 (관리자 전용)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/showInfoes/{showInfoId}")
    public ResponseEntity<ApiResponse<ShowInfoDetailResponse>> updateShowInfo(@PathVariable Long showInfoId, @Valid @RequestBody ShowInfoUpdateRequest request) {

        ShowInfoDetailResponse response = showInfoService.updateShowInfo(showInfoId, request);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("공연 정보가 수정되었습니다.", response));
    }

    /**
     * 공연 삭제 (관리자 전용 - 논리 삭제)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/showInfoes/{showInfoId}")
    public ResponseEntity<ApiResponse<Void>> deleteShowInfo(@PathVariable Long showInfoId) {

        showInfoService.deleteShowInfo(showInfoId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("공연 정보가 삭제되었습니다."));
    }
}
