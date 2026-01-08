package com.example.sixpark.domain.showschedule.repository;

import com.example.sixpark.domain.showinfo.entity.ShowInfo;
import com.example.sixpark.domain.showplace.entity.ShowPlace;
import com.example.sixpark.domain.showschedule.entiry.ShowSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ShowScheduleRepository extends JpaRepository<ShowSchedule, Long>  {

    // 범위로 ShowSchedule 조회
    @Query("SELECT ss FROM ShowSchedule ss WHERE ss.id BETWEEN :start AND :end")
    List<ShowSchedule> findAllByRange(@Param("start") Long startScheduleId, @Param("end") Long endScheduleId);

    // 존재 여부
    boolean existsByShowInfoAndShowPlaceAndShowDateAndShowTime(ShowInfo info, ShowPlace place, LocalDate date, LocalTime time);
}
