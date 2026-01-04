package com.example.sixpark.domain.seat.entity;

import com.example.sixpark.domain.showinfo.entity.ShowInfo;
import com.example.sixpark.domain.showplace.entity.ShowPlace;
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
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_info_id")
    private ShowInfo showinfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_time_id")
    private ShowPlace showtime;

    public Seat(ShowInfo showinfo, ShowPlace showtime) {
        this.showinfo = showinfo;
        this.showtime = showtime;
    }
}
