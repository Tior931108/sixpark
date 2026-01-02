package com.example.sixpark.domain.user.controller;

import com.example.sixpark.common.response.ApiResponse;
import com.example.sixpark.common.security.jwt.JwtProvider;
import com.example.sixpark.common.security.userDetail.AuthUser;
import com.example.sixpark.domain.user.model.request.UserLoginRequest;
import com.example.sixpark.domain.user.model.request.UserPasswordChangeRequest;
import com.example.sixpark.domain.user.model.request.UserSignupRequest;
import com.example.sixpark.domain.user.model.request.UserUpdateRequest;
import com.example.sixpark.domain.user.model.response.UserGetResponse;
import com.example.sixpark.domain.user.model.response.UserLoginResponse;
import com.example.sixpark.domain.user.model.response.UserSignupResponse;
import com.example.sixpark.domain.user.model.response.UserUpdateResponse;
import com.example.sixpark.domain.user.repository.UserRepository;
import com.example.sixpark.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    /**
     * 회원가입 API
     *
     */
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

    /**
     * 내 정보 조회 API
     */
    @GetMapping("/api/users")
    public ResponseEntity<ApiResponse<UserGetResponse>> getMyInfo(@AuthenticationPrincipal AuthUser user) {
        return ResponseEntity.ok(ApiResponse.success("내 정보 조회 성공", (userService.getMyInfo(user.getUserId()))));
    }

    /**
     * 유저 정보 수정 API
     */
    @PutMapping("/api/users")
    public ResponseEntity<ApiResponse<UserUpdateResponse>> updateMyInfo(@AuthenticationPrincipal AuthUser authUser, @RequestBody @Valid UserUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("회원정보 수정 완료", userService.updateMyInfo(authUser.getUserId(), request)));
    }

    /**
     * 비밀번호 변경 API
     */
    @PutMapping("/api/users/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@AuthenticationPrincipal AuthUser authUser, @RequestBody @Valid UserPasswordChangeRequest request) {
        userService.changePassword(authUser.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("비밀번호 변경 완료"));
    }

}
