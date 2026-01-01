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
    private ShowInfo showInfo;

    private String area;

    private String fcltyNm;

    private Long seatScale;

    private LocalTime time;

    public ShowTime(ShowInfo showInfo, String area, String fcltyNm, Long seatScale, LocalTime time) {
        this.showInfo = showInfo;
        this.area = area;
        this.fcltyNm = fcltyNm;
        this.seatScale = seatScale;
        this.time = time;
    }

}
