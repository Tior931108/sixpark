package com.example.sixpark.domain.showinfo.model.response;

import com.example.sixpark.domain.genre.model.dto.GenreDto;
import com.example.sixpark.domain.genre.model.response.GenreResponse;
import com.example.sixpark.domain.showinfo.entity.ShowInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShowInfoRankingResponse {

    private Long id;
    private GenreResponse genre;
    private String mt20id;
    private String prfnm;          // 공연 제목
    private String prfcast;        // 출연진
    private LocalDate prfpdfrom;   // 공연 시작일
    private LocalDate prfpdto;     // 공연 종료일
    private String poster;         // 포스터 URL
    private Long viewCount;        // 조회수
    private Integer rank;          // 순위

    // rank는 redis 에서 다루는 데이터이기에 기본 생성자로 구현 (정적 팩토리 구현 불가)
    public ShowInfoRankingResponse(ShowInfo showInfo, Long viewCount, Integer rank) {
        this.id = showInfo.getId();
        this.genre = GenreResponse.from(GenreDto.from(showInfo.getGenre()));
        this.mt20id = showInfo.getMt20id();
        this.prfnm = showInfo.getPrfnm();
        this.prfcast = showInfo.getPrfcast();
        this.prfpdfrom = showInfo.getPrfpdfrom();
        this.prfpdto = showInfo.getPrfpdto();
        this.poster = showInfo.getPoster();
        this.viewCount = viewCount;
        this.rank = rank;
    }
}
