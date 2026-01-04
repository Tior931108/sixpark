package com.example.sixpark.domain.comment.model.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentGetQueryDto {

    private Long id;
    private Long postId;
    private Long writerId;
    private String nickname;
    private String content;
    private Long parentId;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    @QueryProjection
    public CommentGetQueryDto(Long id, Long postId, Long writerId, String nickname, String content, Long parentId, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.postId = postId;
        this.writerId = writerId;
        this.nickname = nickname;
        this.content = content;
        this.parentId = parentId;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}