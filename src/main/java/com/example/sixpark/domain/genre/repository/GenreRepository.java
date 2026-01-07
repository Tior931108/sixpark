package com.example.sixpark.domain.genre.repository;


import com.example.sixpark.domain.genre.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GenreRepository extends JpaRepository<Genre, Long> {

    Optional<Genre> findByGenrenm(String genrenm);

    // 저장된 장르 갯수 반환
    @Query("SELECT g.id FROM Genre g")
    List<Long> findAllGenreIds();

    boolean existsByGenrenm(String genrenm);
}
