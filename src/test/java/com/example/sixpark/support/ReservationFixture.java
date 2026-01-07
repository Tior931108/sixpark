package com.example.sixpark.support;

import com.example.sixpark.domain.reservation.entity.Reservation;
import com.example.sixpark.domain.seat.entity.Seat;
import com.example.sixpark.domain.user.entity.User;

public class ReservationFixture {

    public static Reservation createReservation() {
        User user = UserFixture.createUser();
        Seat seat = SeatFixture.createSeat();

        return new Reservation(
                user,
                seat
        );
    }
}