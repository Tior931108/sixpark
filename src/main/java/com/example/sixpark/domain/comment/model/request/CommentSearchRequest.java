package com.example.sixpark.domain.comment.model.request;

import lombok.Getter;

@Getter
public class CommentSearchRequest {
    private Long postId;
    private String searchKey;
    private String sort;
}
