package com.example.sixpark.domain.comment.model.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentParentGetQueryDto {

    private Long id;
    private Long postId;
    private Long writerId;
    private String nickname;
    private String content;
    private Long childCommentCount;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    @QueryProjection
    public CommentParentGetQueryDto(Long id, Long postId, Long writerId, String nickname, String content, Long childCommentCount, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.postId = postId;
        this.writerId = writerId;
        this.nickname = nickname;
        this.content = content;
        this.childCommentCount = childCommentCount;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}