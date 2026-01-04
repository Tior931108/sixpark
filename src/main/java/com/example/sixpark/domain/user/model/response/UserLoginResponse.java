package com.example.sixpark.domain.user.model.response;

import lombok.Getter;

@Getter
public class UserLoginResponse {

    private final String accessToken;

    private UserLoginResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    public static UserLoginResponse from(String accessToken) {
        return new UserLoginResponse(accessToken);
    }
}
