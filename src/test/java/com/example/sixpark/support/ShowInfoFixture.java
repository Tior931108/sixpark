package com.example.sixpark.support;

import com.example.sixpark.domain.genre.entity.Genre;
import com.example.sixpark.domain.showinfo.entity.ShowInfo;
import java.time.LocalDate;

public class ShowInfoFixture {

    private static final String DEFAULT_MT20ID = "PF000000";
    private static final String DEFAULT_PRFNM = "테스트 연주회";
    private static final String DEFAULT_PRFCAST = "음악가1, 음악가2";
    private static final LocalDate DEFAULT_PRFPDFROM = LocalDate.of(2026, 1, 10);
    private static final LocalDate DEFAULT_PRFPDTO = LocalDate.of(2026, 1, 10);
    private static final String DEFAULT_POSTER = "http://www.test.gif";
    private static final int DEFAULT_PCSEGUIDANCE = 500;

    public static ShowInfo createShowInfo() {
        Genre genre = GenreFixture.createGenre();

        return new ShowInfo(
                genre,
                DEFAULT_MT20ID,
                DEFAULT_PRFNM,
                DEFAULT_PRFCAST,
                DEFAULT_PRFPDFROM,
                DEFAULT_PRFPDTO,
                DEFAULT_POSTER,
                DEFAULT_PCSEGUIDANCE
        );
    }
}