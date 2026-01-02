package com.example.sixpark.domain.post.model.response;

import com.example.sixpark.domain.post.model.dto.PostDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class PostCreateResponse {

    private final Long id;
    private final Long userId;
    private final Long showInfoId;
    private final String title;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public static PostCreateResponse from(PostDto postDto) {
        return new PostCreateResponse(
                postDto.getId(),
                postDto.getUserId(),
                postDto.getShowInfoId(),
                postDto.getTitle(),
                postDto.getContent(),
                postDto.getCreatedAt(),
                postDto.getModifiedAt()
        );
    }
}
