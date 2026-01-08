package com.example.sixpark.domain.user.model.response;

import com.example.sixpark.domain.user.model.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserUpdateResponse {

    private final String name;
    private final String nickname;

    public static UserUpdateResponse from(UserDto user) {
        return new UserUpdateResponse(user.getName(), user.getNickname());
    }
}
