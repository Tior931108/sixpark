package com.example.sixpark.domain.reservation.service;

import com.example.sixpark.common.enums.ErrorMessage;
import com.example.sixpark.common.excepion.CustomException;
import com.example.sixpark.domain.reservation.entity.Reservation;
import com.example.sixpark.domain.reservation.medel.dto.ReservationDto;
import com.example.sixpark.domain.reservation.medel.request.ReservationCreateRequest;
import com.example.sixpark.domain.reservation.medel.response.ReservationCreateResponse;
import com.example.sixpark.domain.reservation.medel.response.ReservationGetInfoResponse;
import com.example.sixpark.domain.reservation.repository.ReservationRepository;
import com.example.sixpark.domain.seat.entity.Seat;
import com.example.sixpark.domain.seat.repository.SeatRepository;
import com.example.sixpark.domain.user.entity.User;
import com.example.sixpark.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final SeatRepository seatRepository;

    /**
     * 예매 생성
     * @param userId 로그인한 유저
     * @param request 예매 생성 요청 DTO
     * @return 예매 생성 응답 DTO
     */
    public ReservationCreateResponse createReservation(Long userId, ReservationCreateRequest request) {
        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new CustomException(ErrorMessage.NOT_FOUND_USER));
        // 좌석 조회
        Seat seat = seatRepository.findById(request.getSeatId())
                .orElseThrow(()-> new CustomException(ErrorMessage.NOT_FOUND_RESERVATION));

        Reservation reservation = new Reservation(user, seat);
        reservationRepository.save(reservation);

        return ReservationCreateResponse.from(ReservationDto.from(reservation));
    }

    /**
     * 예매 상세조회(본인) API 비지니스 로직
     */
    @Transactional(readOnly = true)
    public Page<ReservationGetInfoResponse> getMyReservations(
            Long userId, Boolean isDeleted, LocalDate startDate, LocalDate endDate, Pageable pageable
    ) {
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;

        return reservationRepository.findMyReservations(userId, isDeleted, startDateTime, endDateTime, pageable);
    }

    /**
     * 예매 취소
     * @param bookId 예매 ID
     */
    public void deleteReservation(Long userId, Long bookId) {
        // 예매 조회
        Reservation reservation = reservationRepository.findById(bookId)
                .orElseThrow(()-> new CustomException(ErrorMessage.NOT_DELETE_AUTHORIZED));

        if (reservation.isDeleted()) // 이미 취소된 예매인지 확인
            throw new CustomException(ErrorMessage.ALREADY_CANCELED_RESERVATION);

        if (reservation.getUser().getId().equals(userId)) // 본인 예매가 맞는지 확인
            throw new CustomException(ErrorMessage.NOT_FOUND_RESERVATION);

        reservation.softDelete(); // 논리 삭제
    }
}
