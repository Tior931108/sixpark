package com.example.sixpark.domain.reservation.medel.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class ReservationGetInfoResponse {

    private final Long id;
    private final Long userId;
    private final int seatNo;
    private final Long showPlaceId;
    private final LocalDateTime createdAt;
    private final boolean canceled;

}
