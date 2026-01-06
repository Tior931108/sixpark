package com.example.sixpark.domain.reservation.medel.dto;

import com.example.sixpark.domain.reservation.entity.Reservation;
import com.example.sixpark.domain.seat.entity.Seat;
import com.example.sixpark.domain.user.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReservationDto {
    private final Long id;
    private final User user;
    private final Seat seat;
    private final int count;

    public static ReservationDto from(Reservation reservation) {
        return new ReservationDto(
                reservation.getId(),
                reservation.getUser(),
                reservation.getSeat(),
                reservation.getCount()
        );
    }
}
