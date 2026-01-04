package com.example.sixpark.domain.comment.controller;

import com.example.sixpark.common.response.ApiResponse;
import com.example.sixpark.common.response.PageResponse;
import com.example.sixpark.common.response.SliceResponse;
import com.example.sixpark.domain.comment.model.request.CommentCreateRequest;
import com.example.sixpark.domain.comment.model.request.CommentSearchRequest;
import com.example.sixpark.domain.comment.model.request.CommentUpdateRequest;
import com.example.sixpark.domain.comment.model.response.CommentCreateResponse;
import com.example.sixpark.domain.comment.model.response.CommentResponse;
import com.example.sixpark.domain.comment.model.response.CommentSearchResponse;
import com.example.sixpark.domain.comment.model.response.CommentUpdateResponse;
import com.example.sixpark.domain.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 생성
     * @param request 게시글 id, 댓글 내용, 부모 댓글 id를 포함한 요청 DTO
     *                - parentId가 null이면 일반 댓글
     *                - parentId가 있으면 대댓글
     * @return 댓글 생성 결과
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CommentCreateResponse>> createComment(
            // Todo @AuthenticationPrincipal AuthUser authUser
            @Valid @RequestBody CommentCreateRequest request
    ) {
        Long authUser = 1L;
        CommentCreateResponse response = commentService.createComment(authUser, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("댓글이 생성되었습니다.", response));
    }

    /**
     * 댓글 수정
     * @param commentId 댓글 아이디
     * @param request 댓글 내용을 포함한 요청 DTO
     * @return 댓글 수정 결과
     */
    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentUpdateResponse>> updateComment(
            // Todo @AuthenticationPrincipal AuthUser authUser
            @PathVariable Long commentId,
            @Valid @RequestBody CommentUpdateRequest request
    ) {
        Long authUser = 1L;
        CommentUpdateResponse response = commentService.updateComment(authUser, commentId, request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("댓글이 수정되었습니다.", response));
    }

    /**
     * 댓글 삭제
     * @param commentId 댓글 아이디
     * @return 댓글 삭제 결과
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            // Todo @AuthenticationPrincipal AuthUser authUser
            @PathVariable Long commentId
    ) {
        Long authUser = 1L;
        commentService.deleteComment(authUser, commentId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("댓글이 삭제되었습니다"));
    }

    /**
     * 댓글 검색
     * @param request 게시글 id, 검색 내용을 포함한 요청 DTO
     * @param pageable 페이징
     * @return 댓글 검색 결과
     */
    @GetMapping("/search")
    public ResponseEntity<PageResponse<CommentSearchResponse>> getSearchComment(
            @ModelAttribute CommentSearchRequest request,
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        PageResponse<CommentSearchResponse> response = commentService.getSearchComment(request, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 부모 댓글 조회
     * @param postId 게시글 id
     * @param pageable 페이징(slice)
     * @return 부모 댓글 조회 결과
     */
    @GetMapping
    public ResponseEntity<SliceResponse<CommentResponse>> getParentComment(
            @RequestParam Long postId,
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        SliceResponse<CommentResponse> response = commentService.getParentComment(postId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 자식 댓글 조회
     * @param parentCommentId 부모 댓글 id
     * @param postId 게시글 id
     * @param pageable 페이징(slice)
     * @return 대댓글 조회 결과
     */
    @GetMapping("/{parentCommentId}/child-comments")
    public ResponseEntity<SliceResponse<CommentResponse>> getChildComment(
            @PathVariable Long parentCommentId,
            @RequestParam Long postId,
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        SliceResponse<CommentResponse> response = commentService.getChildComment(parentCommentId, postId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}