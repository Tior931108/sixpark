package com.example.sixpark.domain.seat.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SeatCreateRequest {

    @NotNull(message = "startScheduleId를 입력해주세요.")
    private Long startScheduleId;

    @NotNull(message = "endScheduleId를 입력해주세요.")
    private Long endScheduleId;

}
