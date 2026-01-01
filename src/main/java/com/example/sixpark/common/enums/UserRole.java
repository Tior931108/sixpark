package com.example.sixpark.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    ADMIN("ROLE_ADMIN", "관리자 권한"),
    USER("ROLE_USER", "일반 사용자 권한")
    ;

    private final String role;        // Spring Security 권한 이름
    private final String description; // 권한 설명
}
