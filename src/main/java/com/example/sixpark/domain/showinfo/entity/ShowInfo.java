package com.example.sixpark.domain.showinfo.entity;

import com.example.sixpark.domain.genre.entity.Genre;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import static java.lang.Boolean.FALSE;

@Entity
@Getter
@Table(name = "show_infoes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShowInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "genre_id")
    private Genre genre;

    @Column(length = 30 ,nullable = false)
    private String mt20id;
    private String prfNm;
    private List<String> prfCast;
    private LocalDate prfpdFrom;
    private LocalDate prfpdTo;
    private String poster;
    private Integer pcseguidanse;
    private boolean isDeleted = false;

    public ShowInfo(String mt20id, String prfNm, List<String> prfCast, LocalDate prfpdFrom, LocalDate prfpdTo, String poster, Integer pcseguidanse) {
        this.mt20id = mt20id;
        this.prfNm = prfNm;
        this.prfCast = prfCast;
        this.prfpdFrom = prfpdFrom;
        this.prfpdTo = prfpdTo;
        this.poster = poster;
        this.pcseguidanse = pcseguidanse;
    }

    public void softDelete() {
        this.isDeleted = true;
    }

}
