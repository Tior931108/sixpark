package com.example.sixpark.domain.reservation.controller;

import com.example.sixpark.common.response.ApiResponse;
import com.example.sixpark.common.response.PageResponse;
import com.example.sixpark.common.security.userDetail.AuthUser;
import com.example.sixpark.domain.reservation.medel.request.ReservationCreateRequest;
import com.example.sixpark.domain.reservation.medel.response.ReservationCreateResponse;
import com.example.sixpark.domain.reservation.medel.response.ReservationGetInfoResponse;
import com.example.sixpark.domain.reservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

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

    /**
     * 예매 상세조회(본인) API 비지니스 로직
     */
    @GetMapping("/book")
    public ResponseEntity<PageResponse<ReservationGetInfoResponse>> getMyReservations(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam(required = false) Boolean status, // true = 취소
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Page<ReservationGetInfoResponse> result = reservationService.getMyReservations(
                authUser.getUserId(),
                status,
                startDate,
                endDate,
                pageable
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(PageResponse.success("예매 목록을 조회했습니다.", result));
    }
}
