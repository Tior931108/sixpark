package com.example.sixpark.domain.showtime.repository;


import com.example.sixpark.domain.showtime.entity.ShowTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ShowTimeRepository extends JpaRepository<ShowTime, Long> {

    // ShowInfo ID로 ShowTime 조회 (1:1 관계이므로 Optional 반환)
    Optional<ShowTime> findByShowInfoId(Long showInfoId);

    // ShowInfo ID로 ShowTime 삭제
    @Modifying
    @Query("DELETE FROM ShowTime st WHERE st.showInfo.id = :showInfoId")
    void deleteByShowInfoId(@Param("showInfoId") Long showInfoId);
}
