package com.example.sixpark.domain.reservation.medel.response;

import com.example.sixpark.domain.reservation.entity.Reservation;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class ReservationGetInfoResponse {

    private final Long id;
    private final Long userId;
    private final Long seatId;
    private final Long showPlaceId;
    private final LocalDateTime createdAt;
    private final boolean canceled;

    public static ReservationGetInfoResponse from(Reservation reservation) {
        return new ReservationGetInfoResponse(
                reservation.getId(),
                reservation.getUser().getId(),
                reservation.getSeat().getId(),
                reservation.getSeat().getShowPlace().getId(),
                reservation.getCreatedAt(),
                reservation.isDeleted()
        );
    }
}
