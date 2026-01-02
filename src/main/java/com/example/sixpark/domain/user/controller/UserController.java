package com.example.sixpark.domain.user.controller;

import com.example.sixpark.common.response.ApiResponse;
import com.example.sixpark.domain.user.model.request.UserSignupRequest;
import com.example.sixpark.domain.user.model.response.UserSignupResponse;
import com.example.sixpark.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/api/auth/signup")
    public ResponseEntity<ApiResponse<UserSignupResponse>> signup(@RequestBody @Valid UserSignupRequest request) {
        return ResponseEntity.ok(ApiResponse.success("회원가입이 완료되었습니다.", userService.signup(request)));
    }
}
