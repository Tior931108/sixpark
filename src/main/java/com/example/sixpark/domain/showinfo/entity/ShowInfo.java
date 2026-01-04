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

    @Column(nullable = false, length = 30, unique = true)
    private String mt20id; // KOPIS api 공연ID

    @Column(nullable = false)
    private String prfnm; // 공연 제목

    @Column(length = 500)
    private String prfcast; // 공연 출연진

    @Column(nullable = false)
    private LocalDate prfpdfrom; // 공연 시작 날짜

    @Column(nullable = false)
    private LocalDate prfpdto; // 공연 종료 날짜

    @Column(columnDefinition = "TEXT")
    private String poster; // 포스터 이미지 링크

    @Column(nullable = false)
    private Integer pcseguidance; // 좌석 가격

    @Column(nullable = false, length = 10)
    private boolean isDeleted = false; // 논리 삭제 여부

    public ShowInfo(Genre genre, String mt20id, String prfnm, String prfcast, LocalDate prfpdfrom, LocalDate prfpdto, String poster, Integer pcseguidance) {
        this.genre = genre;
        this.mt20id = mt20id;
        this.prfnm = prfnm;
        this.prfcast = prfcast;
        this.prfpdfrom = prfpdfrom;
        this.prfpdto = prfpdto;
        this.poster = poster;
        this.pcseguidance = pcseguidance;
    }

    public static ShowInfo create(Genre genre, String mt20id, String prfnm, String prfcast,
                                  LocalDate prfpdfrom, LocalDate prfpdto,
                                  String poster, Integer pcseguidance) {
        ShowInfo showInfo = new ShowInfo();
        showInfo.genre = genre;
        showInfo.mt20id = mt20id;
        showInfo.prfnm = prfnm;
        showInfo.prfcast = prfcast;
        showInfo.prfpdfrom = prfpdfrom;
        showInfo.prfpdto = prfpdto;
        showInfo.poster = poster;
        showInfo.pcseguidance = pcseguidance;
        return showInfo;
    }

    public void softDelete() {
        this.isDeleted = true;
    }

}
