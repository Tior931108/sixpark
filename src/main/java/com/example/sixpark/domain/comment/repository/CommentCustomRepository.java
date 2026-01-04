package com.example.sixpark.domain.comment.repository;

import com.example.sixpark.domain.comment.model.dto.CommentGetQueryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CommentCustomRepository {
    // 댓글 검색
    Page<CommentGetQueryDto> getSearchComments(Long postId, String searchKey, Pageable pageable);

    // 부모 댓글 조회
    Slice<CommentGetQueryDto> getParentComment(Long postId, Pageable pageable);

    // 자식 댓글 조회
    Slice<CommentGetQueryDto> getChildComment(Long parentCommentId, Long postId, Pageable pageable);
}
