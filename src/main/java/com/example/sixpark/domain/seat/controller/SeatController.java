package com.example.sixpark.domain.seat.controller;

import com.example.sixpark.common.response.ApiResponse;
import com.example.sixpark.domain.seat.model.request.SeatCreateRequest;
import com.example.sixpark.domain.seat.model.request.SeatSelectRequest;
import com.example.sixpark.domain.seat.model.response.SeatSelectResponse;
import com.example.sixpark.domain.seat.service.SeatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SeatController {

    private final SeatService seatService;

    /**
     * 좌석 생성
     * @param request 스케줄 ID 범위
     * @return 생성 메시지, 200 OK
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/seat")
    public ResponseEntity<ApiResponse<Void>> createSeat(
            @Valid @RequestBody SeatCreateRequest request
    ) {
        seatService.createSeat(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("좌석이 생성되었습니다."));
    }

    /**
     * 좌석 선택
     * @param request 좌석 선택 요청 DTO
     * @return 좌석 선택 응답 DTO
     */
    @PostMapping("/book/seat")
    public ResponseEntity<ApiResponse<SeatSelectResponse>> selectSeat(
            @Valid @RequestBody SeatSelectRequest request
    ) {
        SeatSelectResponse result = seatService.selectSeat(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("좌석이 선택되었습니다.", result));
    }
}
