package com.example.sixpark.domain.genre.entity;

import com.example.sixpark.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "genre")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String genrenm;

    @Column(nullable = false, updatable = false)
    private LocalDate createdAt;

    public Genre(String genrenm) {
        this.genrenm = genrenm;
    }

    public static Genre create(String genrenm) {
        Genre genre = new Genre();
        genre.genrenm = genrenm;
        genre.createdAt = LocalDate.now();
        return genre;
    }

    // 장르 생성 시간
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDate.now();
        }
    }

}
