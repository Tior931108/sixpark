package com.example.sixpark.support;

import com.example.sixpark.domain.showinfo.entity.ShowInfo;
import com.example.sixpark.domain.showplace.entity.ShowPlace;
import com.example.sixpark.domain.showschedule.entiry.ShowSchedule;

import java.time.LocalDate;
import java.time.LocalTime;

public class ShowScheduleFixture {

    private static final LocalDate DEFAULT_SHOWDATE = LocalDate.of(2026, 1, 10);
    private static final LocalTime DEFAULT_SHOWTIME = LocalTime.of(12, 20, 0);

    public static ShowSchedule createShowSchedule() {
        ShowInfo showInfo = ShowInfoFixture.createShowInfo();
        ShowPlace showPlace = ShowPlaceFixture.createShowPlace();

        return new ShowSchedule(
                showInfo,
                showPlace,
                DEFAULT_SHOWDATE,
                DEFAULT_SHOWTIME
        );
    }
}