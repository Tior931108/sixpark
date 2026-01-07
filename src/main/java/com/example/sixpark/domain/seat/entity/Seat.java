package com.example.sixpark.domain.seat.entity;

import com.example.sixpark.domain.showschedule.entiry.ShowSchedule;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name="seats")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seat {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 좌석 Id

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "show_place_id")
    private ShowSchedule showSchedule; // 공연 스케줄 정보

    @Column(nullable = false)
    private int seatNo; // 좌석번호

    private boolean isSelected = false; // 좌석 선택 여부

    public Seat(ShowSchedule showSchedule, int seatNo) {
        this.showSchedule = showSchedule;
        this.seatNo = seatNo;
    }

    /**
     * 좌석 선택
     * @param isSelected 선택 여부
     */
    public void select(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
