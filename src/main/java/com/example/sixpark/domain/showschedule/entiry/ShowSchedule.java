package com.example.sixpark.domain.showschedule.entiry;

import com.example.sixpark.domain.showinfo.entity.ShowInfo;
import com.example.sixpark.domain.showplace.entity.ShowPlace;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Table(name="show_schedules")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShowSchedule {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 스케줄 ID

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "show_info_id")
    private ShowInfo showInfo; // 공연 정보

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "show_place_id")
    private ShowPlace showPlace; // 공연 장소

    @Column(nullable = false)
    private LocalDate showDate; // 공연 날짜

    @Column(nullable = false)
    private LocalTime showTime; // 공연 시간

    public ShowSchedule(ShowInfo showInfo, ShowPlace showPlace, LocalDate showDate, LocalTime showTime) {
        this.showInfo = showInfo;
        this.showPlace = showPlace;
        this.showDate = showDate;
        this.showTime = showTime;
    }

}
