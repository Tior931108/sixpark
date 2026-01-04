package com.example.sixpark.domain.genre.service;

import com.example.sixpark.domain.genre.entity.Genre;
import com.example.sixpark.domain.genre.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;

    /**
     * 장르 생성 또는 조회
     * 장르명이 이미 존재하면 기존 장르 반환, 없으면 새로 생성
     */
    @Transactional(readOnly = true)
    public Genre getOrCreateGenre(String genrenm) {
        return genreRepository.findByGenrenm(genrenm)
                .orElseGet(() -> {
                    Genre newGenre = Genre.create(genrenm);
                    Genre savedGenre = genreRepository.save(newGenre);
                    log.info("새로운 장르 생성: {} (ID: {})", genrenm, savedGenre.getId());
                    return savedGenre;
                });
    }
}
