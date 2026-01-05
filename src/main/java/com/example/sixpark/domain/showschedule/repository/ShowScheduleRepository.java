package com.example.sixpark.domain.showschedule.repository;

import com.example.sixpark.domain.showschedule.entiry.ShowSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShowScheduleRepository extends JpaRepository<ShowSchedule, Long>  {
}
