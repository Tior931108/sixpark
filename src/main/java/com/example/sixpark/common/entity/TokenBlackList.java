package com.example.sixpark.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor
public class TokenBlackList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String token;

    @Column(nullable = false)
    private Instant expiredAt;

    public TokenBlackList(String token, Instant expiredAt) {
        this.token = token;
        this.expiredAt = expiredAt;
    }
}
