package com.example.sixpark.domain.reservation.medel.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ReservationCreateRequest {

    @NotNull
    private Long seatId;

}
