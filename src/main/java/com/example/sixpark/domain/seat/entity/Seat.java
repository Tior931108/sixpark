package com.example.sixpark.domain.seat.entity;

import com.example.sixpark.domain.showplace.entity.ShowPlace;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Table(name="seats")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seat {
    @Id
    private Long id; // 좌석 번호

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "show_place_id")
    private ShowPlace showPlace; // 공연 장소 정보

    @Column(nullable = false)
    private LocalDate showDate; // 공연 날짜

    @Column(nullable = false)
    private LocalTime showTime; // 공연 시간

    private boolean isSelected = false; // 좌석 선택 여부

    public Seat(ShowPlace showPlace, LocalDate showDate, LocalTime showTime) {
        this.showPlace = showPlace;
        this.showDate = showDate;
        this.showTime = showTime;
    }

    /**
     * 좌석 선택
     * @param isSelected 선택 여부
     */
    public void select(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
