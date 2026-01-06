package com.example.sixpark.domain.seat.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SeatSelectRequest {

    @NotNull(message = "공연 스케줄이 선택되지 않았습니다.")
    private Long scheduleId;

    @NotNull(message = "좌석이 선택되지 않았습니다.")
    private int seatNo;

}
