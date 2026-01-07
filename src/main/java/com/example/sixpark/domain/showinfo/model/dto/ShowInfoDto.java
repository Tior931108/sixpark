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
    private Genre genre;
    private String mt20id;
    private String prfnm;
    private String prfcast;
    private LocalDate prfpdfrom;
    private LocalDate prfpdto;
    private String poster;
    private Integer pcseguidance;

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
