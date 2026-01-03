package com.example.sixpark.common.security.jwt;

import com.example.sixpark.common.enums.ErrorMessage;
import com.example.sixpark.common.enums.UserRole;
import com.example.sixpark.common.excepion.CustomException;
import com.example.sixpark.common.security.tokenRepository.TokenBlackListRepository;
import com.example.sixpark.common.security.userDetail.AuthUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

/**
 * 매 요청마다 실행되는 JWT 인증 필터
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final TokenBlackListRepository tokenBlackListRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Authorization 헤더에서 토큰 추출
        String token = resolveToken(request);

        if (token != null) {

            // 토큰이 블랙리스트에 있는지 확인
            if (tokenBlackListRepository.existsByToken(token)) {
                throw new CustomException(ErrorMessage.INVALID_TOKEN);
            }

            // 토큰 검증 (만료/위조 시 예외 발생)
            jwtProvider.validateToken(token);

            // 토큰에서 userId, role 추출
            Long userId = jwtProvider.getUserId(token);
            String role = jwtProvider.getRole(token);
            UserRole userRole = UserRole.valueOf(role);

            // AuthUser 직접 생성 (DB 조회 안함)
            AuthUser authUser = new AuthUser(
                    userId,
                    null,
                    null,
                    userRole
            );

            // Authentication 객체 생성
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            authUser,
                            null,
                            authUser.getAuthorities()
                    );

            // SecurityContext에 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Authorization: Bearer 토큰 파싱
     */
    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");

        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
