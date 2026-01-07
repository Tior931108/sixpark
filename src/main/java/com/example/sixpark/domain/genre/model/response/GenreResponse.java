package com.example.sixpark.domain.genre.model.response;

import com.example.sixpark.domain.genre.model.dto.GenreDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GenreResponse {

    private Long id;
    private String genrenm;

    public static GenreResponse from(GenreDto genre) {
        return new GenreResponse(
                genre.getId(),
                genre.getGenrenm()
        );
    }
}
