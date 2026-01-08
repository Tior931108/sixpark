package com.example.sixpark.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 로그아웃 API에 필요한 토큰 블랙리스트 엔티티
 */
@Entity
@Getter
@NoArgsConstructor
@Table(name = "token_blackList", indexes = {@Index(name = "idx_token_blackList", columnList = "token")})
public class TokenBlackList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    public TokenBlackList(String token, LocalDateTime expiredAt) {
        this.token = token;
        this.expiredAt = expiredAt;
    }
}
