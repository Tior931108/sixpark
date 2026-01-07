package com.example.sixpark.support;

import com.example.sixpark.domain.user.entity.User;
import java.time.LocalDate;

public class UserFixture {
    private static final String DEFAULT_EMAIL = "test1@example.com";
    private static final String DEFAULT_PASSWORD = "password1";
    private static final String DEFAULT_NAME = "유저1";
    private static final String DEFAULT_NICKNAME = "닉네임1";
    private static final LocalDate DEFAULT_BIRTH = LocalDate.of(2000, 5, 10);

    public static User createUser() {
        return new User(
                DEFAULT_EMAIL,
                DEFAULT_PASSWORD,
                DEFAULT_NAME,
                DEFAULT_NICKNAME,
                DEFAULT_BIRTH
        );
    }
}