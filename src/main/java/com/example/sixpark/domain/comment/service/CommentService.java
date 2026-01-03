package com.example.sixpark.domain.comment.service;

import com.example.sixpark.common.excepion.CustomException;
import com.example.sixpark.common.response.PageResponse;
import com.example.sixpark.domain.comment.entity.Comment;
import com.example.sixpark.domain.comment.model.dto.CommentDto;
import com.example.sixpark.domain.comment.model.dto.CommentSearchQueryDto;
import com.example.sixpark.domain.comment.model.request.CommentCreateRequest;
import com.example.sixpark.domain.comment.model.request.CommentSearchRequest;
import com.example.sixpark.domain.comment.model.request.CommentUpdateRequest;
import com.example.sixpark.domain.comment.model.response.CommentCreateResponse;
import com.example.sixpark.domain.comment.model.response.CommentSearchResponse;
import com.example.sixpark.domain.comment.model.response.CommentUpdateResponse;
import com.example.sixpark.domain.comment.model.response.WriterResponse;
import com.example.sixpark.domain.comment.repository.CommentRepository;
import com.example.sixpark.domain.post.entity.Post;
import com.example.sixpark.domain.post.reository.PostRepository;
import com.example.sixpark.domain.user.entity.User;
import com.example.sixpark.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.sixpark.common.enums.ErrorMessage.*;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    /**
     * 댓글 생성
     * @param authUser 로그인한 유저의 아이디
     * @param request 댓글 생성 요청 DTO
     *                - parentId가 null이면 일반 댓글 생성
     *                - parentId가 있으면 대댓글 생성
     * @return 댓글 생성 결과
     */
    @Transactional
    public CommentCreateResponse createComment(Long authUser, CommentCreateRequest request) {
        User writer = getUserByIdOrThrow(authUser);
        Post post = getPostByIdOrThrow(request.getPostId());

        Comment parent = null;
        if (request.getParentId() != null) {
            parent = getCommentByIdOrThrow(request.getParentId());
            invalidParentComment(parent, post);
        }

        Comment comment = commentRepository.save(new Comment(request.getContent(), post, writer, parent));

        CommentDto dto = CommentDto.from(comment);

        return CommentCreateResponse.from(dto, WriterResponse.from(writer));
    }

    /**
     * 부모 댓글 검증
     * @param parent 부모 댓글
     * @param post 댓글 작성 게시글
     */
    private static void invalidParentComment(Comment parent, Post post) {
        // 해당 게시글에 부모 댓글이 없으면 예외처리 발생
        if(!parent.getPost().getId().equals(post.getId())) {
            throw new CustomException(NOT_CORRECT_PARAMETER);
        }
        // 이미 부모 댓글이 있으면 대대댓글은 쓸수없도록 즉 대댓글까지만 가능하도록 예외처리
        if(parent.getParentComment() != null) {
            throw new CustomException(NOT_CORRECT_PARAMETER);
        }
    }

    /**
     * 유저 아이디에 해당하는 유저 조회 없으면 예외 발생
     * @param id 유저 아이디
     * @return 조회된 유저 엔티티
     */
    private User getUserByIdOrThrow(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new CustomException(NOT_FOUND_USER)
        );
    }

    /**
     * 게시글 아이디에 해당하는 게시글 조회 없으면 예외 발생
     * @param id 게시글 아이디
     * @return 조회된 게시글 엔티티
     */
    private Post getPostByIdOrThrow(Long id) {
        return postRepository.findById(id).orElseThrow(
                () -> new CustomException(NOT_FOUND_TASK) // 나중에 상수를 NOT_FOUND_POST로 변경
        );
    }

    /**
     * 댓글 아이디에 해당하는 댓글 조회 없으면 예외 발생
     * @param id 댓글 아이디
     * @return 조회된 댓글 엔티티
     */
    private Comment getCommentByIdOrThrow(Long id) {
        return commentRepository.findById(id).orElseThrow(
                () -> new CustomException(NOT_FOUND_COMMENT)
        );
    }

    /**
     * 댓글 수정
     * @param authUser 로그인한 유저의 아이디
     * @param commentId 댓글 아이디
     * @param request 댓글 수정 요청 DTO
     * @return 댓글 수정 결과
     */
    @Transactional
    public CommentUpdateResponse updateComment(Long authUser, Long commentId, CommentUpdateRequest request) {
        User writer = getUserByIdOrThrow(authUser);
        Comment comment = getCommentByIdOrThrow(commentId);
        matchedWriter(writer.getId(), comment.getUser().getId());
        comment.update(request.getContent());
        commentRepository.save(comment);
        CommentDto dto = CommentDto.from(comment);
        return CommentUpdateResponse.from(dto, WriterResponse.from(writer));
    }

    /**
     * 작성자가 일치하지 않으면 예외 발생
     * @param userId 유저 아이디
     * @param commentUserId 댓글작성자 아이디
     */
    private static void matchedWriter(Long userId, Long commentUserId) {
        if(!userId.equals(commentUserId)) {
            throw new CustomException(NOT_MODIFY_AUTHORIZED);
        }
    }

    /**
     * 댓글 삭제
     * @param authUser 유저 아이디
     * @param commentId 댓글 아이디
     */
    @Transactional
    public void deleteComment(Long authUser, Long commentId) {
        User writer = getUserByIdOrThrow(authUser);
        Comment comment = getCommentByIdOrThrow(commentId);
        matchedWriter(writer.getId(), comment.getUser().getId());
        comment.softDelete();
    }

    @Transactional(readOnly = true)
    public PageResponse<CommentSearchResponse> getAllComment(CommentSearchRequest request, Pageable pageable) {
        Post post = getPostByIdOrThrow(request.getPostId());
        Sort sortOption = request.getSort().equalsIgnoreCase("newest")
                ? Sort.by("createdAt").descending() : Sort.by("createdAt").ascending();

        Pageable finalPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sortOption
        );

        Page<CommentSearchQueryDto> commentList = commentRepository.getComments(post.getId(), request.getSearchKey(), finalPageable);

        Page<CommentSearchResponse> commentPageList = commentList.map(dto ->
                new CommentSearchResponse(
                        dto.getId(),
                        dto.getPostId(),
                        dto.getWriterId(),
                        new WriterResponse(
                                dto.getWriterId(),
                                dto.getNickname()
                        ),
                        dto.getContent(),
                        dto.getParentId(),
                        dto.getCreatedAt(),
                        dto.getModifiedAt()
                )
        );
        return PageResponse.success("댓글 목록 조회 성공", commentPageList);
    }
}