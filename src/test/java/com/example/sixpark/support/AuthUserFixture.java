package com.example.sixpark.support;

import com.example.sixpark.common.enums.UserRole;
import com.example.sixpark.common.security.userDetail.AuthUser;

public class AuthUserFixture {
    private static final long DEFAULT_ID = 1L;
    private static final String DEFAULT_EMAIL = "test1@example.com";
    private static final String DEFAULT_PASSWORD = "password1";
    private static final UserRole DEFAULT_ROLE = UserRole.USER;

    public static AuthUser createAuthUser() {
        return new AuthUser(
                DEFAULT_ID,
                DEFAULT_EMAIL,
                DEFAULT_PASSWORD,
                DEFAULT_ROLE
        );
    }
}
