package com.example.sixpark.domain.showinfo.model.dto;

import com.example.sixpark.domain.genre.entity.Genre;
import com.example.sixpark.domain.showinfo.entity.ShowInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ShowInfoDto {

    private Long id;
    private Genre genre;        // 장르
    private String mt20id;      // KOPIS API 공연 ID
    private String prfnm;       // 공연 제목
    private String prfcast;     // 출연진
    private LocalDate prfpdfrom;// 공연 시작날짜
    private LocalDate prfpdto;  // 공연 종료날짜
    private String poster;      // 포스터 이미지
    private Integer pcseguidance; // 공연 일정

    public static ShowInfoDto from(ShowInfo showInfo) {
        return new ShowInfoDto(
                showInfo.getId(),
                showInfo.getGenre(),
                showInfo.getMt20id(),
                showInfo.getPrfnm(),
                showInfo.getPrfcast(),
                showInfo.getPrfpdfrom(),
                showInfo.getPrfpdto(),
                showInfo.getPoster(),
                showInfo.getPcseguidance()
        );
    }
}
