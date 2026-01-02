package com.example.sixpark.domain.comment.controller;

import com.example.sixpark.common.response.ApiResponse;
import com.example.sixpark.domain.comment.model.request.CommentCreateRequest;
import com.example.sixpark.domain.comment.model.response.CommentCreateResponse;
import com.example.sixpark.domain.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    // 댓글 생성
    @PostMapping
    public ResponseEntity<ApiResponse<CommentCreateResponse>> createComment(
            // Todo @AuthenticationPrincipal AuthUser authUser
            @Valid @RequestBody CommentCreateRequest request
    ) {
        Long authUser = 1L;
        CommentCreateResponse response = commentService.createComment(authUser, request); // todo 로그인 유저의 아이디 값만 서비스로
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("댓글이 생성되었습니다.", response));
    }
}