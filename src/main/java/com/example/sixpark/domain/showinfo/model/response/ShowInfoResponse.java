package com.example.sixpark.domain.showinfo.model.response;

import com.example.sixpark.domain.genre.model.dto.GenreDto;
import com.example.sixpark.domain.genre.model.response.GenreResponse;
import com.example.sixpark.domain.showinfo.model.dto.ShowInfoDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShowInfoResponse {

    private Long id;
    private GenreResponse genre;
    private String mt20id;
    private String prfnm;
    private String prfcast;
    private LocalDate prfpdfrom;
    private LocalDate prfpdto;
    private String poster;
    private Integer pcseguidance;

    public static ShowInfoResponse from(ShowInfoDto showInfo) {
        return new ShowInfoResponse(
                showInfo.getId(),
                GenreResponse.from(GenreDto.from(showInfo.getGenre())),
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
