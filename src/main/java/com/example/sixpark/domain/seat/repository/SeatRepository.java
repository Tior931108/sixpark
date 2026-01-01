package com.example.sixpark.domain.seat.repository;

import com.example.sixpark.domain.seat.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, Long> {
}
