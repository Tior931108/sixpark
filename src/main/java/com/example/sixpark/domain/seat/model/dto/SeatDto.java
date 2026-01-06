package com.example.sixpark.domain.seat.model.dto;

import com.example.sixpark.domain.seat.entity.Seat;
import com.example.sixpark.domain.showplace.entity.ShowPlace;
import com.example.sixpark.domain.showschedule.entiry.ShowSchedule;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@RequiredArgsConstructor
public class SeatDto {

    private final Long id;
    private final ShowSchedule showSchedule; // 공연 스케줄
    private final boolean isSelected;

    public static SeatDto from(Seat seat) {
        return new SeatDto(
                seat.getId(),
                seat.getShowSchedule(),
                seat.isSelected()
        );
    }
}
