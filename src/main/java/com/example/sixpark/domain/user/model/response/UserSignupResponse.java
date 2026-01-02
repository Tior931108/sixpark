package com.example.sixpark.domain.user.model.response;

import com.example.sixpark.domain.user.model.dto.UserDto;
import lombok.Getter;

@Getter
public class UserSignupResponse {

    private final UserDto user;

    private UserSignupResponse(UserDto user) {
        this.user = user;
    }

    public static UserSignupResponse from(UserDto user) {
        return new UserSignupResponse(user);
    }
}
