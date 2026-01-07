package com.example.sixpark.domain.showplace.model.dto;

import com.example.sixpark.domain.showplace.entity.ShowPlace;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShowPlaceDto {

    private Long id;
    private String area;           // 지역
    private String fcltynm;        // 시설명
    private Long seatscale;        // 좌석 수
    private String dtguidance;     // 공연 시간 정보
    private String prfruntime;     // 공연 총 시간


    public static ShowPlaceDto from(ShowPlace showPlace) {
        return new ShowPlaceDto(
                showPlace.getId(),
                showPlace.getArea(),
                showPlace.getFcltynm(),
                showPlace.getSeatscale(),
                showPlace.getDtguidance(),
                showPlace.getPrfruntime()
        );
    }
    
}
