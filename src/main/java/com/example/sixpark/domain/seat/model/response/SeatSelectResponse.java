package com.example.sixpark.domain.seat.model.response;

import com.example.sixpark.domain.seat.model.dto.SeatDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SeatSelectResponse {

    private final Long seatId;

    public static SeatSelectResponse from(SeatDto dto) {
        return new SeatSelectResponse(
                dto.getId()
        );
    }
}
