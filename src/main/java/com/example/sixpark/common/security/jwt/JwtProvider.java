package com.example.sixpark.common.security.jwt;

import com.example.sixpark.common.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {

    /**
     * application.yml에서 주입
     * jwt.secret.key: ${JWT_SECRET_KEY}
     */
    @Value("${jwt.secret.key}")
    private String secretKey;

    // Access Token 만료 시간 (60분)
    private final long TOKEN_EXPIRE_TIME = 1000L * 60 * 60;

    /**
     * JWT 서명 키 생성
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    /**
     * JWT 토큰 생성
     */
    public String createToken(Long userId, UserRole role) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId)) // 토큰의 주인 (userId)
                .claim("role", role.name())                 // 권한 정보
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRE_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 토큰 검증
     * - 만료 / 위조 / 변조 시 예외 발생
     */
    public void validateToken(String token) {
        Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
    }

    /**
     * 토큰에서 Claims 추출
     */
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long getUserId(String token) {
        return Long.valueOf(getClaims(token).getSubject());
    }

    public String getRole(String token) {
        return getClaims(token).get("role", String.class);
    }
}
