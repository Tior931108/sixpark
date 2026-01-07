package com.example.sixpark.domain.seat.model.dto;

import com.example.sixpark.domain.seat.entity.Seat;
import com.example.sixpark.domain.showschedule.entiry.ShowSchedule;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SeatDto {

    private final Long id;
    private final ShowSchedule showSchedule; // 공연 스케줄
    private final int seatNo; // 좌석 번호
    private final boolean isSelected;

    public static SeatDto from(Seat seat) {
        return new SeatDto(
                seat.getId(),
                seat.getShowSchedule(),
                seat.getSeatNo(),
                seat.isSelected()
        );
    }
}
