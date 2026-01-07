package com.example.sixpark.domain.post.controller;

import com.example.sixpark.common.response.ApiResponse;
import com.example.sixpark.common.response.PageResponse;
import com.example.sixpark.common.security.userDetail.AuthUser;
import com.example.sixpark.domain.post.model.request.PostCreateRequest;
import com.example.sixpark.domain.post.model.request.PostUpdateRequest;
import com.example.sixpark.domain.post.model.response.PostCreateResponse;
import com.example.sixpark.domain.post.model.response.PostGetAllResponse;
import com.example.sixpark.domain.post.model.response.PostGetOneResponse;
import com.example.sixpark.domain.post.model.response.PostUpdateResponse;
import com.example.sixpark.domain.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostController {

    private final PostService postService;

    @PostMapping("/posts")
    public ResponseEntity<ApiResponse<PostCreateResponse>> createPost(@Valid @RequestBody PostCreateRequest request, @AuthenticationPrincipal AuthUser authUser) {
        PostCreateResponse response = postService.createPost(request, authUser);
        return ResponseEntity.ok(ApiResponse.success("게시글이 생성되었습니다", response));
    }

    @GetMapping("/posts")
    public ResponseEntity<PageResponse<PostGetAllResponse>> getPostList(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "createdAt") String sort, @RequestParam(defaultValue = "desc") String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        Page<PostGetAllResponse> posts = postService.getPostList(pageable);
        return ResponseEntity.ok(PageResponse.success("게시글 목록 조회 성공", posts));
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<PostGetOneResponse>> getPost(@PathVariable Long postId) {
        PostGetOneResponse response = postService.getPost(postId);
        return ResponseEntity.ok(ApiResponse.success("게시글 조회 성공", response));
    }

    @PutMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<PostUpdateResponse>> updatePost(@PathVariable Long postId, @Valid @RequestBody PostUpdateRequest request, @AuthenticationPrincipal AuthUser authUser) {
        PostUpdateResponse response = postService.updatePost(authUser.getUserId(), postId, request);
        return ResponseEntity.ok(ApiResponse.success("게시글이 수정되었습니다.", response));
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable Long postId, @AuthenticationPrincipal AuthUser authUser) {
        postService.deletePost(authUser.getUserId(), postId);
        return ResponseEntity.ok(ApiResponse.success("게시글이 삭제되었습니다."));
    }
}
