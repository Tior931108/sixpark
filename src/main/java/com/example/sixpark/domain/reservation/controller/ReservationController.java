package com.example.sixpark.domain.reservation.controller;

import com.example.sixpark.common.response.ApiResponse;
import com.example.sixpark.common.security.userDetail.AuthUser;
import com.example.sixpark.domain.reservation.medel.request.ReservationCreateRequest;
import com.example.sixpark.domain.reservation.medel.response.ReservationCreateResponse;
import com.example.sixpark.domain.reservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReservationController {
    private final ReservationService reservationService;

    /**
     * 예매 생성
     * @param authUser 로그인한 유저
     * @param request 예매 생성 요청 DTO
     * @return 예매 생성 응답 DTO
     */
    @PostMapping("/book")
    public ResponseEntity<ApiResponse<ReservationCreateResponse>> createReservation(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody ReservationCreateRequest request
    ) {
        ReservationCreateResponse result = reservationService.createReservation(authUser.getUserId(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("예매가 완료되었습니다.", result));
    }

    /**
     * 예매 취소
     * @param authUser 로그인한 유저
     * @param bookId 예매 ID
     * @return 200 OK
     */
    @DeleteMapping("/book/{bookId}")
    public ResponseEntity<ApiResponse<Void>> deleteReservation(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long bookId
    ) {
        reservationService.deleteReservation(authUser.getUserId(), bookId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("예매가 취소되었습니다."));
    }
}
