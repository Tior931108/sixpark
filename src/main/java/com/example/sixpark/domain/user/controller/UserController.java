package com.example.sixpark.domain.user.controller;

import com.example.sixpark.common.response.ApiResponse;
import com.example.sixpark.common.security.jwt.JwtProvider;
import com.example.sixpark.common.security.userDetail.AuthUser;
import com.example.sixpark.domain.user.model.request.UserLoginRequest;
import com.example.sixpark.domain.user.model.request.UserSignupRequest;
import com.example.sixpark.domain.user.model.response.UserLoginResponse;
import com.example.sixpark.domain.user.model.response.UserSignupResponse;
import com.example.sixpark.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    @PostMapping("/api/auth/signup")
    public ResponseEntity<ApiResponse<UserSignupResponse>> signup(@RequestBody @Valid UserSignupRequest request) {
        return ResponseEntity.ok(ApiResponse.success("회원가입이 완료되었습니다.", userService.signup(request)));
    }

    /**
     * 로그인 API
     */
    @PostMapping("/api/auth/login")
    public ResponseEntity<ApiResponse<UserLoginResponse>> login(@RequestBody @Valid UserLoginRequest request) {
        // 이메일 + 비밀번호로 Authentication 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                );

        // Spring Security 인증 처리
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // 인증된 사용자 정보 추출
        // principal = CustomUserDetailsService에서 반환한 AuthUser
        AuthUser authUser = (AuthUser) authentication.getPrincipal();

        // JWT Access Token 생성
        String accessToken = jwtProvider.createToken(
                authUser.getUserId(),          // userId
                authUser.getRole()   // USER / ADMIN
        );

        // 응답 반환
        return ResponseEntity.ok(ApiResponse.success("로그인 성공", UserLoginResponse.from(accessToken)));
    }
}
