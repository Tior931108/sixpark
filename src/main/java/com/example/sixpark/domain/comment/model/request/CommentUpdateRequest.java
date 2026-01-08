package com.example.sixpark.domain.comment.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CommentUpdateRequest {
    @NotBlank(message = "댓글 내용은 필수 입니다.")
    @Size(max = 255, message = "225이하로 작성해주세요")
    private String content;
}