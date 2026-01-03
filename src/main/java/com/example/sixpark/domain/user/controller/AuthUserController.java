package com.example.sixpark.domain.user.controller;

import com.example.sixpark.common.response.ApiResponse;
import com.example.sixpark.common.response.PageResponse;
import com.example.sixpark.common.security.jwt.JwtProvider;
import com.example.sixpark.common.security.userDetail.AuthUser;
import com.example.sixpark.domain.user.model.dto.UserDto;
import com.example.sixpark.domain.user.model.request.UserLoginRequest;
import com.example.sixpark.domain.user.model.request.UserRoleChangeRequest;
import com.example.sixpark.domain.user.model.request.UserSignupRequest;
import com.example.sixpark.domain.user.model.response.UserLoginResponse;
import com.example.sixpark.domain.user.model.response.UserSignupResponse;
import com.example.sixpark.domain.user.repository.UserRepository;
import com.example.sixpark.domain.user.service.AuthUesrService;
import com.example.sixpark.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthUserController {

    private final AuthUesrService authUesrService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    /**
     * 회원가입 API
     */
    @PostMapping("/api/auth/signup")
    public ResponseEntity<ApiResponse<UserSignupResponse>> signup(@RequestBody @Valid UserSignupRequest request) {
        return ResponseEntity.ok(ApiResponse.success("회원가입이 완료되었습니다.", authUesrService.signup(request)));
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
     * 로그아웃 API
     */
    @PostMapping("/api/auth/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        authUesrService.logout(token);

        return ResponseEntity.ok(ApiResponse.success("로그아웃 되었습니다."));
    }

    /**
     * 관리자 권한 유저 전체 페이징 조회 API
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/admin")
    public ResponseEntity<PageResponse<UserDto>> getAllUsers(@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<UserDto> page = authUesrService.getAllUsers(pageable);

        return ResponseEntity.ok(PageResponse.success("전체 회원 페이징 조회 성공", page));
    }

    /**
     * 관리자 권한 유저 권한 변경 API
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/api/admin/users/{userId}")
    public ResponseEntity<ApiResponse<Void>> changeUserRole(@PathVariable Long userId, @RequestBody UserRoleChangeRequest request) {
        authUesrService.changeUserRole(userId, request.getRole());
        return ResponseEntity.ok(ApiResponse.success("권한이 변경되었습니다."));
    }

}
