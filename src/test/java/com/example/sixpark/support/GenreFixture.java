package com.example.sixpark.support;

import com.example.sixpark.domain.genre.entity.Genre;

public class GenreFixture {

    private static final String DEFAULT_GENRE = "서양음악(클래식)";

    public static Genre createGenre() {
        return new Genre(DEFAULT_GENRE);
    }
}
