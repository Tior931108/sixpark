package com.example.sixpark.domain.comment.repository;

import com.example.sixpark.domain.comment.model.dto.CommentSearchQueryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentCustomRepository {
    Page<CommentSearchQueryDto> getComments(Long postId, String searchKey, Pageable pageable);
}
