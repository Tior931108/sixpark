package com.example.sixpark.domain.showtime.repository;


import com.example.sixpark.domain.showtime.entity.ShowTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShowTimeRepository extends JpaRepository<ShowTime, Integer> {
}
