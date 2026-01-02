package com.example.sixpark.domain.seat.model.response;

import com.example.sixpark.domain.seat.model.dto.SeatDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SelectSeatResponse {
    private final Long seatId;
    private final Long showtimeId;
    private final Long showInfoId;

    public static SelectSeatResponse from(SeatDto seatDto) {
        return new SelectSeatResponse(
                seatDto.getId(),
                seatDto.getShowtime().getId(),
                seatDto.getShowinfo().getId()
        );
    }
}
