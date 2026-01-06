package com.example.sixpark.domain.comment.model.response;

import com.example.sixpark.domain.user.model.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WriterResponse {
    private final Long id;
    private final String nickname;

    public static WriterResponse from(UserDto writer) {
        return new WriterResponse(
                writer.getId(),
                writer.getNickname()
        );
    }
}