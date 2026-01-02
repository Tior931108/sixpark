package com.example.sixpark.domain.post.controller;

import com.example.sixpark.common.response.ApiResponse;
import com.example.sixpark.domain.post.model.request.PostCreateRequest;
import com.example.sixpark.domain.post.model.response.PostCreateResponse;
import com.example.sixpark.domain.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<ApiResponse<PostCreateResponse>> createPost(
            @RequestParam Long userId,
            @RequestParam Long showInfoId,
            @Valid @RequestBody PostCreateRequest request) {

        PostCreateResponse response = postService.createPost(userId, showInfoId, request);
        return ResponseEntity.ok(ApiResponse.success("게시글이 생성되었습니다", response));
    }
}
