package com.example.sixpark.domain.seat.entity;

import com.example.sixpark.domain.showinfo.entity.ShowInfo;
import com.example.sixpark.domain.showtime.entity.ShowTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name="seats")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seat {
    @Id
    private Long id; // 좌석 번호

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "show_time_id")
    private ShowTime showtime; // 공연 시간

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "show_info_id")
    private ShowInfo showinfo; // 공연 정보

    private boolean isSelected = false; // 좌석 선택 여부

    public Seat(ShowInfo showinfo, ShowTime showtime) {
        this.showinfo = showinfo;
        this.showtime = showtime;
    }

    /**
     * 좌석 선택
     * @param isSelected 선택 여부
     */
    public void select(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
