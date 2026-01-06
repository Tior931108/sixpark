package com.example.sixpark.domain.seat.controller;

import com.example.sixpark.common.response.ApiResponse;
import com.example.sixpark.domain.seat.model.request.CreateSeatRequest;
import com.example.sixpark.domain.seat.model.request.SelectSeatRequest;
import com.example.sixpark.domain.seat.model.response.SelectSeatResponse;
import com.example.sixpark.domain.seat.service.SeatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SeatController {

    private final SeatService seatService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/seat")
    public ResponseEntity<ApiResponse<Void>> createSeat(
            @Valid @RequestBody List<CreateSeatRequest> request
    ) {
        seatService.createSeat(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("좌석이 생성되었습니다."));
    }

    /**
     * 좌석 선택
     * @param request 좌석 선택 요청 DTO (좌석, 공연시간)
     * @return 좌석 선택 응답 DTO (좌석, 공연시간, 공연정보)
     */
    @PostMapping("/book/seat")
    public ResponseEntity<ApiResponse<SelectSeatResponse>> selectSeat(
            @Valid @RequestBody SelectSeatRequest request
    ) {
        SelectSeatResponse result = seatService.selectSeat(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("좌석이 선택되었습니다.", result));
    }
}
