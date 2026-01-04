package com.example.sixpark.domain.reservation.controller;

import com.example.sixpark.common.response.ApiResponse;
import com.example.sixpark.domain.reservation.medel.request.ReservationCreateRequest;
import com.example.sixpark.domain.reservation.medel.response.ReservationCreateResponse;
import com.example.sixpark.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReservationController {
    private final ReservationService reservationService;

    /**
     * 예매 생성
     * @param userId 로그인한 유저
     * @param request 예매 생성 요청 DTO
     * @return 예매 생성 응답 DTO
     */
    @PostMapping("/book")
    public ResponseEntity<ApiResponse<ReservationCreateResponse>> createReservation(
            @PathVariable Long userId, // todo 변경해야함
            @RequestBody ReservationCreateRequest request
    ) {
        ReservationCreateResponse result = reservationService.createReservation(userId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("", result));
    }
}
