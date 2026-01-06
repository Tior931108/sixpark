package com.example.sixpark.domain.seat.model.response;

import com.example.sixpark.domain.seat.model.dto.SeatDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SelectSeatResponse {

    private final Long seatId;

    public static SelectSeatResponse from(SeatDto dto) {
        return new SelectSeatResponse(
                dto.getId()
        );
    }
}
