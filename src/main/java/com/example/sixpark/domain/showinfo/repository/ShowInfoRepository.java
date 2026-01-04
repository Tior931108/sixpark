package com.example.sixpark.domain.showinfo.repository;

import com.example.sixpark.domain.showinfo.entity.ShowInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ShowInfoRepository extends JpaRepository<ShowInfo, Long> {

    boolean existsByMt20id(String mt20id);

    Optional<ShowInfo> findByMt20id(String mt20id);

    // N+1 문제 해결: Genre를 Fetch Join으로 조회
    @Query("SELECT s FROM ShowInfo s JOIN FETCH s.genre WHERE s.mt20id = :mt20id")
    Optional<ShowInfo> findByMt20idWithGenre(@Param("mt20id") String mt20id);
}
