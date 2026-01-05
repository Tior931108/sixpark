package com.example.sixpark.domain.showinfo.repository;

import com.example.sixpark.domain.showinfo.entity.ShowInfo;
import com.example.sixpark.domain.showinfo.model.dto.ShowInfoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    // 전체 공연 페이징 조회 (삭제되지 않은 공연만) - N+1 해결
    @Query(value = "SELECT s FROM ShowInfo s JOIN FETCH s.genre WHERE s.isDeleted = false ",
            countQuery = "SELECT COUNT(s) FROM ShowInfo s WHERE s.isDeleted = false ")
    Page<ShowInfo> findAllActiveWithGenre(Pageable pageable);

    // 장르별 공연 페이징 조회 (삭제되지 않은 공연만) - N+1 해결
    @Query(value = "SELECT s FROM ShowInfo s JOIN FETCH s.genre g WHERE g.id = :genreId AND s.isDeleted = false ",
            countQuery = "SELECT COUNT(s) FROM ShowInfo s WHERE s.genre.id = :genreId AND s.isDeleted = false ")
    Page<ShowInfo> findByGenreIdWithGenre(@Param("genreId") Long genreId, Pageable pageable);

    // ID로 공연 상세 조회 (Genre Fetch Join)
    @Query("SELECT s FROM ShowInfo s JOIN FETCH s.genre WHERE s.id = :id AND s.isDeleted = false")
    Optional<ShowInfo> findByIdWithGenre(@Param("id") Long id);
}


