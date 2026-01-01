package com.example.sixpark.domain.genre.repository;


import com.example.sixpark.domain.genre.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Integer> {
}
