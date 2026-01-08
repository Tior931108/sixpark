package com.example.sixpark.domain.reservation.repository;

import com.example.sixpark.domain.reservation.entity.Reservation;
import com.example.sixpark.domain.seat.entity.Seat;
import com.example.sixpark.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, ReservationCustomRepository {

    boolean existsByUserAndSeatAndIsDeletedFalse(User user, Seat seat);
}
