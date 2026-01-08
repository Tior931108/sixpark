package com.example.sixpark.support;

import com.example.sixpark.domain.seat.entity.Seat;
import com.example.sixpark.domain.showschedule.entiry.ShowSchedule;

public class SeatFixture {

    private static final int DEFAULT_SEATNO = 1;

    public static Seat createSeat() {
        ShowSchedule showSchedule = ShowScheduleFixture.createShowSchedule();

        return new Seat(
                showSchedule,
                DEFAULT_SEATNO
        );
    }
}