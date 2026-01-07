package com.example.sixpark.domain.reservation.repository;

import com.example.sixpark.domain.reservation.medel.response.ReservationGetInfoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;

public interface ReservationCustomRepository {

    Page<ReservationGetInfoResponse> findMyReservations(
            Long userId,
            Boolean isDeleted,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    );
}
