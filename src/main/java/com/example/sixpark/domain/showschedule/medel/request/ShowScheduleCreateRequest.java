package com.example.sixpark.domain.showschedule.medel.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ShowScheduleCreateRequest {

    @NotNull(message = "startShowPlaceId를 입력해주세요.")
    private Long startPlaceId;

    @NotNull(message = "endShowPlaceId를 입력해주세요.")
    private Long endPlaceId;

}
