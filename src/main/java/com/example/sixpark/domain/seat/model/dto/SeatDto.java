package com.example.sixpark.domain.seat.model.dto;

import com.example.sixpark.domain.seat.entity.Seat;
import com.example.sixpark.domain.showplace.entity.ShowPlace;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@RequiredArgsConstructor
public class SeatDto {

    private final Long id;
    private final ShowPlace showPlace; // 공연 장소 정보
    private final LocalDate showDate; // 공연 날짜
    private final LocalTime seatTime; // 공연 시간
    private final boolean isSelected;

    public static SeatDto from(Seat seat) {
        return new SeatDto(
                seat.getId(),
                seat.getShowPlace(),
                seat.getShowDate(),
                seat.getShowTime(),
                seat.isSelected()
        );
    }
}
