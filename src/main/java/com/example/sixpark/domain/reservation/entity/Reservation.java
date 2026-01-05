package com.example.sixpark.domain.reservation.entity;

import com.example.sixpark.common.entity.BaseEntity;
import com.example.sixpark.domain.seat.entity.Seat;
import com.example.sixpark.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name="reservations")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seat_id")
    private Seat seat;

    private int count = 1; // 1인 1티켓

    @Column(nullable = false)
    private boolean isDeleted = false;

    public Reservation(User user, Seat seat) {
        this.user = user;
        this.seat = seat;
    }

    public void softDelete() {
        this.isDeleted = true;
    }
}
