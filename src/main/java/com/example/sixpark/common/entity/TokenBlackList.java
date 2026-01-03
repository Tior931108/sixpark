package com.example.sixpark.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * 로그아웃 API에 필요한 토큰 블랙리스트 엔티티
 */
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
