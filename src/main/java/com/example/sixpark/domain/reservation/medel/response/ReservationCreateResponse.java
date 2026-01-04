package com.example.sixpark.domain.reservation.medel.response;

import com.example.sixpark.domain.reservation.medel.dto.ReservationDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReservationCreateResponse {

    private final Long reservationId;
    private final Long userId;
    private final Long seatId;

    public static ReservationCreateResponse from(ReservationDto dto) {
        return new ReservationCreateResponse(
                dto.getId(),
                dto.getUser().getId(),
                dto.getSeat().getId()
        );
    }
}
