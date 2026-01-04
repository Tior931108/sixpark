package com.example.sixpark.domain.comment.model.request;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class CommentSearchRequest {
    private Long postId;
    private String searchKey;
}
