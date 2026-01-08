package com.example.sixpark.domain.showplace.entity;

import com.example.sixpark.common.entity.BaseEntity;
import com.example.sixpark.domain.showinfo.entity.ShowInfo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
// 검색 성능 개선 인덱스 적용 - v2 전용
@Table(name = "show_places", indexes = {@Index(name = "idx_show_places_area", columnList = "area"), @Index(name = "idx_show_places_fcltynm", columnList = "fcltynm"), @Index(name = "idx_show_places_dtguidance", columnList = "dtguidance")})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShowPlace extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_info_id", unique = true) // 공연정보 - 공연시간정보 1:1 보장
    private ShowInfo showInfo; // 공연정보 ID

    @Column(nullable = false, length = 100)
    private String area; // 지역

    @Column(nullable = false, length = 500)
    private String fcltynm; // 시설 이름

    @Column(nullable = false)
    private Long seatscale; // 좌석 수

    @Column(nullable = false)
    private String dtguidance; // 공연 시작시간 ex) 금요일(18:00,20:30), 토요일 ~ 일요일(17:00,19:30)

    @Column(nullable = false)
    private String prfruntime; // 공연 총시간 ex) 1시간 30분

    @Column(nullable = false, length = 10)
    private boolean isDeleted = false; // 논리 삭제 여부

    public ShowPlace(ShowInfo showInfo, String area, String fcltynm, Long seatscale, String dtguidance, String prfruntime) {
        this.showInfo = showInfo;
        this.area = area;
        this.fcltynm = fcltynm;
        this.seatscale = seatscale;
        this.dtguidance = dtguidance;
        this.prfruntime = prfruntime;
    }

    public static ShowPlace create(ShowInfo showInfo, String area, String fcltynm,
                                   Long seatscale, String dtguidance, String prfruntime) {
        ShowPlace showPlace = new ShowPlace();
        showPlace.showInfo = showInfo;
        showPlace.area = area;
        showPlace.fcltynm = fcltynm;
        showPlace.seatscale = seatscale;
        showPlace.dtguidance = dtguidance;
        showPlace.prfruntime = prfruntime;
        return showPlace;
    }

    public void softDelete() {
        this.isDeleted = true;
    }
}
