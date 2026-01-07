package com.example.sixpark.domain.showinfo.model.response;

import com.example.sixpark.domain.showinfo.entity.ShowInfo;
import lombok.Getter;
import java.time.LocalDate;

@Getter
public class ShowInfoSearchResponse {

    // Genre 정보
    private String genreName;    // 장르 제목

    // ShowInfo 정보
    private String mt20id;       // 공연 ID
    private String prfnm;        // 공연 제목
    private String prfcast;      // 출연진
    private LocalDate prfpdfrom; // 공연 시작 날짜
    private LocalDate prfpdto;   // 공연 종료 날짜
    private String poster;       // 이미지

    // ShowPlace 정보
    private String area;         // 지역
    private String fcltynm;      // 시설 이름
    private String dtguidance;   // 공연 일정



    public ShowInfoSearchResponse(ShowInfo showInfo) {

        // Genre 정보
        this.genreName = showInfo.getGenre().getGenrenm();

        // ShowInfo 정보
        this.mt20id = showInfo.getMt20id();
        this.prfnm = showInfo.getPrfnm();
        this.prfcast = showInfo.getPrfcast();
        this.prfpdfrom = showInfo.getPrfpdfrom();
        this.prfpdto = showInfo.getPrfpdto();
        this.poster = showInfo.getPoster();

        // ShowPlace 정보
        if (showInfo.getShowPlace() != null) {
            this.area = showInfo.getShowPlace().getArea();
            this.fcltynm = showInfo.getShowPlace().getFcltynm();
            this.dtguidance = showInfo.getShowPlace().getDtguidance();
        }
    }
}
