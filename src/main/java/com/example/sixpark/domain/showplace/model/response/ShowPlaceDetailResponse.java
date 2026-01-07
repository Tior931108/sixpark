package com.example.sixpark.domain.showplace.model.response;

import com.example.sixpark.domain.showplace.model.dto.ShowPlaceDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShowPlaceDetailResponse {

    private Long id;
    private String area;           // 지역
    private String fcltynm;        // 시설명
    private Long seatscale;        // 좌석 수
    private String dtguidance;     // 공연 시간 정보
    private String prfruntime;     // 공연 총 시간

    public static ShowPlaceDetailResponse from(ShowPlaceDto showPlace) {
        return new ShowPlaceDetailResponse(
                showPlace.getId(),
                showPlace.getArea(),
                showPlace.getFcltynm(),
                showPlace.getSeatscale(),
                showPlace.getDtguidance(),
                showPlace.getPrfruntime()
        );
    }
}
