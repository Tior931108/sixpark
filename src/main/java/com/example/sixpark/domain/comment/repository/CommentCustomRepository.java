package com.example.sixpark.domain.comment.repository;

import com.example.sixpark.domain.comment.model.dto.CommentChildGetQueryDto;
import com.example.sixpark.domain.comment.model.dto.CommentParentGetQueryDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CommentCustomRepository {
    // 댓글 검색
    Slice<CommentParentGetQueryDto> getSearchComments(Long postId, String searchKey, Pageable pageable);

    // 부모 댓글 조회
    Slice<CommentParentGetQueryDto> getParentComment(Long postId, Pageable pageable);

    // 자식 댓글 조회
    Slice<CommentChildGetQueryDto> getChildComment(Long parentCommentId, Long postId, Pageable pageable);
}
