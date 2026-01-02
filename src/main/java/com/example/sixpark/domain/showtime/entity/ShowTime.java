package com.example.sixpark.domain.showtime.entity;

import com.example.sixpark.common.entity.BaseEntity;
import com.example.sixpark.domain.showinfo.entity.ShowInfo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Getter
@Table(name = "show_times")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShowTime extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_info_id")
    private ShowInfo showInfo; // 공연정보 ID

    @Column(nullable = false, length = 30)
    private String area; // 지역

    @Column(nullable = false, length = 30)
    private String fcltynm; // 시설 이름

    @Column(nullable = false)
    private Long seatscale; // 좌석 수

    @Column(nullable = false)
    private LocalTime time; // 공연 시간

    public ShowTime(ShowInfo showInfo, String area, String fcltynm, Long seatscale, LocalTime time) {
        this.showInfo = showInfo;
        this.area = area;
        this.fcltynm = fcltynm;
        this.seatscale = seatscale;
        this.time = time;
    }

    public static ShowTime create(ShowInfo showInfo, String area, String fcltynm,
                                  Long seatscale, LocalTime time) {
        ShowTime showTime = new ShowTime();
        showTime.showInfo = showInfo;
        showTime.area = area;
        showTime.fcltynm = fcltynm;
        showTime.seatscale = seatscale;
        showTime.time = time;
        return showTime;
    }

}
