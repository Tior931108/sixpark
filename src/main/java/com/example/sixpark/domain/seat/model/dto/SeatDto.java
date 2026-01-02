package com.example.sixpark.domain.seat.model.dto;

import com.example.sixpark.domain.seat.entity.Seat;
import com.example.sixpark.domain.showinfo.entity.ShowInfo;
import com.example.sixpark.domain.showtime.entity.ShowTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SeatDto {

    private final Long id;
    private final ShowTime showtime;
    private final ShowInfo showinfo;
    private final boolean isSelected;

    public static SeatDto from(Seat seat) {
        return new SeatDto(
                seat.getId(),
                seat.getShowtime(),
                seat.getShowinfo(),
                seat.isSelected()
        );
    }
}
