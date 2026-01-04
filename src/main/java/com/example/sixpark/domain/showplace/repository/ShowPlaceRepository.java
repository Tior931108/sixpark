package com.example.sixpark.domain.showplace.repository;


import com.example.sixpark.domain.showplace.entity.ShowPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ShowPlaceRepository extends JpaRepository<ShowPlace, Long> {

    // ShowInfo ID로 ShowTime 조회 (1:1 관계이므로 Optional 반환)
    Optional<ShowPlace> findByShowInfoId(Long showInfoId);

    // ShowInfo ID로 ShowPlace 삭제
    @Modifying
    @Query("DELETE FROM ShowPlace st WHERE st.showInfo.id = :showInfoId")
    void deleteByShowInfoId(@Param("showInfoId") Long showInfoId);
}
