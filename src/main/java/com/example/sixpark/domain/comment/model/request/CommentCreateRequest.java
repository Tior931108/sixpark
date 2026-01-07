package com.example.sixpark.domain.comment.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CommentCreateRequest {
    private Long postId;
    @NotBlank(message = "댓글 내용은 필수 입니다.")
    private String content;
    private Long parentId;
}