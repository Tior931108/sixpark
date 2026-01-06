package com.example.sixpark.domain.post.model.dto;

import com.example.sixpark.domain.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostDto {

    private final Long id;
    private final Long userId;
    private final Long showInfoId;
    private final String title;
    private final String content;
    private final boolean isDeleted;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public static PostDto from(Post post) {
        return new PostDto(
                post.getId(),
                post.getUser().getId(),
                post.getShowinfo().getId(),
                post.getTitle(),
                post.getContent(),
                post.isDeleted(),
                post.getCreatedAt(),
                post.getModifiedAt()
        );
    }
}