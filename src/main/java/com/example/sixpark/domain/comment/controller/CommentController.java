package com.example.sixpark.domain.comment.controller;

import com.example.sixpark.common.response.ApiResponse;
import com.example.sixpark.common.response.SliceResponse;
import com.example.sixpark.common.security.userDetail.AuthUser;
import com.example.sixpark.domain.comment.model.request.CommentCreateRequest;
import com.example.sixpark.domain.comment.model.request.CommentUpdateRequest;
import com.example.sixpark.domain.comment.model.response.*;
import com.example.sixpark.domain.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 생성
     * @param request 게시글 id, 댓글 내용, 부모 댓글 id를 포함한 요청 DTO
     *                - parentId가 null이면 일반 댓글
     *                - parentId가 있으면 대댓글
     * @return 댓글 생성 결과
     */
    @PostMapping("/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(@AuthenticationPrincipal AuthUser authUser, @Valid @RequestBody CommentCreateRequest request) {
        CommentResponse response = commentService.createComment(authUser.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("댓글이 생성되었습니다.", response));
    }

    /**
     * 댓글 수정
     * @param commentId 댓글 아이디
     * @param request 댓글 내용을 포함한 요청 DTO
     * @return 댓글 수정 결과
     */
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long commentId, @Valid @RequestBody CommentUpdateRequest request) {
        CommentResponse response = commentService.updateComment(authUser.getUserId(), commentId, request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("댓글이 수정되었습니다.", response));
    }

    /**
     * 댓글 삭제
     * @param commentId 댓글 아이디
     * @return 댓글 삭제 결과
     */
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long commentId) {
        commentService.deleteComment(authUser.getUserId(), commentId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("댓글이 삭제되었습니다"));
    }

    /**
     * 댓글 검색
     * @param postId 게시글 id
     * @param searchKey 검색어
     * @param pageable 페이징
     * @return 댓글 검색 결과
     */
    @GetMapping("/comments/search")
    public ResponseEntity<SliceResponse<CommentParentResponse>> getSearchComment(@RequestParam Long postId, @RequestParam(required = false) String searchKey, @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Slice<CommentParentResponse> response = commentService.getSearchComment(postId, searchKey, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(SliceResponse.success("댓글 검색 성공", response));
    }

    /**
     * 부모 댓글 조회
     * @param postId 게시글 id
     * @param pageable 페이징(slice)
     * @return 부모 댓글 조회 결과
     */
    @GetMapping("/comments")
    public ResponseEntity<SliceResponse<CommentParentResponse>> getParentComment(@RequestParam Long postId, @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Slice<CommentParentResponse> response = commentService.getParentComment(postId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(SliceResponse.success("댓글 조회 성공", response));
    }

    /**
     * 자식 댓글 조회
     * @param parentCommentId 부모 댓글 id
     * @param postId 게시글 id
     * @param pageable 페이징(slice)
     * @return 대댓글 조회 결과
     */
    @GetMapping("/comments/{parentCommentId}/child-comments")
    public ResponseEntity<SliceResponse<CommentChildResponse>> getChildComment(@PathVariable Long parentCommentId, @RequestParam Long postId, @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Slice<CommentChildResponse> response = commentService.getChildComment(parentCommentId, postId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(SliceResponse.success("대댓글 조회 성공", response));
    }
}