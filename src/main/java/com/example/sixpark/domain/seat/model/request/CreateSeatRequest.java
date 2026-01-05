package com.example.sixpark.domain.seat.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateSeatRequest {

    @NotNull(message = "스케줄이 선택되지 않았습니다.")
    private Long scheduleId;

}
