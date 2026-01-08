package com.example.sixpark.domain.seat.repository;

import com.example.sixpark.domain.seat.entity.Seat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    // 비관적 락
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT s FROM Seat s
            JOIN FETCH s.showSchedule ss
            WHERE ss.id = :scheduleId AND s.seatNo = :seatNo
    """)
    Optional<Seat> findSeatForLOCK(@Param("scheduleId") Long scheduleId, @Param("seatNo") int seatNo);

    // 좌석이 존재하는 스케줄 ID
    @Query("""
            SELECT DISTINCT ss.id
            FROM Seat s
            JOIN s.showSchedule ss
            WHERE ss.id IN :scheduleIds
    """)
    List<Long> findExistScheduleIds(List<Long> scheduleIds);

    // 락 없이 조회
    @Query("""
            SELECT s FROM Seat s
            JOIN FETCH s.showSchedule ss
            WHERE ss.id = :scheduleId AND s.seatNo = :seatNo
    """)
    Optional<Seat> findSeat(@Param("scheduleId") Long scheduleId, @Param("seatNo") int seatNo);
}
