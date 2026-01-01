package com.example.sixpark.domain.reservation.repository;

import com.example.sixpark.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
