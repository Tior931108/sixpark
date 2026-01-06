package com.example.sixpark.domain.showschedule.medel.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ShowScheduleCreateRequest {

    @NotNull(message = "공연 장소를 선택해주세요.")
    private Long showPlaceId;

}
