package com.example.sixpark.domain.genre.model.dto;

import com.example.sixpark.domain.genre.entity.Genre;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GenreDto {

    private Long id;
    private String genrenm;
    private LocalDate createdAt;

    public static GenreDto from(Genre genre) {
        return new GenreDto(
                genre.getId(),
                genre.getGenrenm(),
                genre.getCreatedAt()
        );
    }

}
