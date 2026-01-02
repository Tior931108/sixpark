package com.example.sixpark.domain.user.model.response;

import com.example.sixpark.domain.user.model.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserGetResponse {

    private final Long id;
    private final String email;
    private final String name;
    private final String nickname;
    private final String role;

    public static UserGetResponse from(UserDto user) {
        return new UserGetResponse(user.getId(), user.getEmail(), user.getName(), user.getNickname(), user.getRole().name());
    }
}
