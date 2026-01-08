package com.example.sixpark.support;

import com.example.sixpark.domain.showinfo.entity.ShowInfo;
import com.example.sixpark.domain.showplace.entity.ShowPlace;

public class ShowPlaceFixture {

    private static final String DEFAULT_AREA = "서울";
    private static final String DEFAULT_FCLTYNM = "예술의 전당";
    private static final Long DEFAULT_SEATSCALE = 500L;
    private static final String DEFAULT_DTGUIDANCE = "금요일(18:00)";
    private static final String DEFAULT_PRFRUNTICE = "1시간 30분";

    public static ShowPlace createShowPlace() {
        ShowInfo showInfo = ShowInfoFixture.createShowInfo();

        return new ShowPlace(
                showInfo,
                DEFAULT_AREA,
                DEFAULT_FCLTYNM,
                DEFAULT_SEATSCALE,
                DEFAULT_DTGUIDANCE,
                DEFAULT_PRFRUNTICE
        );
    }
}
