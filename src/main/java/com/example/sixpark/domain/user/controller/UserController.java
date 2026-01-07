package com.example.sixpark.domain.user.controller;

import com.example.sixpark.common.response.ApiResponse;
import com.example.sixpark.common.security.userDetail.AuthUser;
import com.example.sixpark.domain.user.model.request.*;
import com.example.sixpark.domain.user.model.response.UserGetResponse;
import com.example.sixpark.domain.user.model.response.UserUpdateResponse;
import com.example.sixpark.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    /**
     * 내 정보 조회 API
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<UserGetResponse>> getMyInfo(@AuthenticationPrincipal AuthUser user) {
        return ResponseEntity.ok(ApiResponse.success("내 정보 조회 성공", (userService.getMyInfo(user.getUserId()))));
    }

    /**
     * 유저 정보 수정 API
     */
    @PutMapping("/users")
    public ResponseEntity<ApiResponse<UserUpdateResponse>> updateMyInfo(@AuthenticationPrincipal AuthUser authUser, @RequestBody @Valid UserUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("회원정보 수정 완료", userService.updateMyInfo(authUser.getUserId(), request)));
    }

    /**
     * 비밀번호 변경 API
     */
    @PutMapping("/users/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@AuthenticationPrincipal AuthUser authUser, @RequestBody @Valid UserPasswordChangeRequest request) {
        userService.changePassword(authUser.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("비밀번호 변경 완료"));
    }

    /**
     * 비밀번호 확인 API
     */
    @PostMapping("/users/verify-password")
    public ResponseEntity<ApiResponse<Void>> checkPassword(@AuthenticationPrincipal AuthUser authUser, @RequestBody @Valid UserPasswordCheckRequest request) {
        userService.checkPassword(authUser.getUserId(), request.getPassword());
        return ResponseEntity.ok(ApiResponse.success("비밀번호가 확인되었습니다."));
    }

    /**
     * 회원 탈퇴 API
     */
    @DeleteMapping("/users")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@AuthenticationPrincipal AuthUser authUser, @RequestHeader("Authorization") String authorizationHeader) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        userService.deleteUser(authUser.getUserId(), accessToken);
        return ResponseEntity.ok(ApiResponse.success("회원 탈퇴가 완료되었습니다."));
    }

}
