package com.example.sixpark.domain.post.controller;

import com.example.sixpark.common.response.ApiResponse;
import com.example.sixpark.common.response.PageResponse;
import com.example.sixpark.domain.post.model.request.PostCreateRequest;
import com.example.sixpark.domain.post.model.response.PostCreateResponse;
import com.example.sixpark.domain.post.model.response.PostGetAllResponse;
import com.example.sixpark.domain.post.model.response.PostGetOneResponse;
import com.example.sixpark.domain.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @GetMapping
    public ResponseEntity<PageResponse<PostGetAllResponse>> getPostList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        Page<PostGetAllResponse> posts = postService.getPostList(pageable);
        return ResponseEntity.ok(PageResponse.success("게시글 목록 조회 성공", posts));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostGetOneResponse>> getPost(
            @PathVariable Long postId) {

        PostGetOneResponse response = postService.getPost(postId);
        return ResponseEntity.ok(ApiResponse.success("게시글 조회 성공", response));
    }
}
