package com.example.sixpark.domain.comment.service;

import com.example.sixpark.common.excepion.CustomException;
import com.example.sixpark.common.response.SliceResponse;
import com.example.sixpark.domain.comment.entity.Comment;
import com.example.sixpark.domain.comment.model.dto.CommentChildGetQueryDto;
import com.example.sixpark.domain.comment.model.dto.CommentDto;
import com.example.sixpark.domain.comment.model.dto.CommentParentGetQueryDto;
import com.example.sixpark.domain.comment.model.request.CommentCreateRequest;
import com.example.sixpark.domain.comment.model.request.CommentSearchRequest;
import com.example.sixpark.domain.comment.model.request.CommentUpdateRequest;
import com.example.sixpark.domain.comment.model.response.*;
import com.example.sixpark.domain.comment.repository.CommentRepository;
import com.example.sixpark.domain.post.entity.Post;
import com.example.sixpark.domain.post.reository.PostRepository;
import com.example.sixpark.domain.user.entity.User;
import com.example.sixpark.domain.user.model.dto.UserDto;
import com.example.sixpark.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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
     * @param userId 로그인한 유저의 아이디
     * @param request 댓글 생성 요청 DTO
     *                - parentId가 null이면 일반 댓글 생성
     *                - parentId가 있으면 대댓글 생성
     * @return 댓글 생성 결과
     */
    @Transactional
    public CommentCreateResponse createComment(Long userId, CommentCreateRequest request) {
        User writer = getUserByIdOrThrow(userId);
        Post post = getPostByIdOrThrow(request.getPostId());

        Comment parent = null;
        if (request.getParentId() != null) {
            parent = getCommentByIdOrThrow(request.getParentId());
            invalidParentComment(parent, post);
        }

        Comment comment = commentRepository.save(new Comment(request.getContent(), post, writer, parent));

        CommentDto dto = CommentDto.from(comment);

        return CommentCreateResponse.from(dto, WriterResponse.from(UserDto.from(writer)));
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
     * @param userId 유저 아이디
     * @return 조회된 유저 엔티티
     */
    private User getUserByIdOrThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new CustomException(NOT_FOUND_USER)
        );
    }

    /**
     * 게시글 아이디에 해당하는 게시글 조회 없으면 예외 발생
     * @param postId 게시글 아이디
     * @return 조회된 게시글 엔티티
     */
    private Post getPostByIdOrThrow(Long postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new CustomException(NOT_FOUND_POST) // 나중에 상수를 NOT_FOUND_POST로 변경
        );
    }

    /**
     * 댓글 아이디에 해당하는 댓글 조회 없으면 예외 발생
     * @param commentId 댓글 아이디
     * @return 조회된 댓글 엔티티
     */
    private Comment getCommentByIdOrThrow(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new CustomException(NOT_FOUND_COMMENT)
        );
    }

    /**
     * 댓글 수정
     * @param userId 로그인한 유저의 아이디
     * @param commentId 댓글 아이디
     * @param request 댓글 수정 요청 DTO
     * @return 댓글 수정 결과
     */
    @Transactional
    public CommentUpdateResponse updateComment(Long userId, Long commentId, CommentUpdateRequest request) {
        User writer = getUserByIdOrThrow(userId);
        Comment comment = getCommentByIdOrThrow(commentId);
        matchedWriter(writer.getId(), comment.getUser().getId());
        comment.update(request.getContent());
        commentRepository.save(comment);
        CommentDto dto = CommentDto.from(comment);
        return CommentUpdateResponse.from(dto, WriterResponse.from(UserDto.from(writer)));
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
     * @param userId 유저 아이디
     * @param commentId 댓글 아이디
     */
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        User writer = getUserByIdOrThrow(userId);
        Comment comment = getCommentByIdOrThrow(commentId);
        matchedWriter(writer.getId(), comment.getUser().getId());
        comment.softDelete();
    }

    /**
     * 댓글 검색
     * @param request 댓글 검색 요청 dto
     * @param pageable 페이징
     * @return 댓글 검색 결과
     */
    @Transactional(readOnly = true)
    public SliceResponse<CommentParentResponse> getSearchComment(CommentSearchRequest request, Pageable pageable) {
        Post post = getPostByIdOrThrow(request.getPostId());

        Slice<CommentParentGetQueryDto> commentList = commentRepository.getSearchComments(post.getId(), request.getSearchKey(), pageable);

        Slice<CommentParentResponse> commentPageList = commentList.map(dto ->
                new CommentParentResponse(
                        dto.getId(),
                        dto.getPostId(),
                        dto.getWriterId(),
                        new WriterResponse(
                                dto.getWriterId(),
                                dto.getNickname()
                        ),
                        dto.getContent(),
                        dto.getChildCommentCount(),
                        dto.getCreatedAt(),
                        dto.getModifiedAt()
                )
        );
        return SliceResponse.success("댓글 검색 조회 성공", commentPageList);
    }

    /**
     * 부모 댓글 조회
     * @param postId 게시글 id
     * @param pageable 페이징(slice)
     * @return 부모 조회 결과
     */
    @Transactional(readOnly = true)
    public SliceResponse<CommentParentResponse> getParentComment(Long postId, Pageable pageable) {
        Post post = getPostByIdOrThrow(postId);
        Slice<CommentParentGetQueryDto> parentCommentList = commentRepository.getParentComment(post.getId(), pageable);

        Slice<CommentParentResponse> parentCommentSliceList = parentCommentList.map(dto ->
                new CommentParentResponse(
                        dto.getId(),
                        dto.getPostId(),
                        dto.getWriterId(),
                        new WriterResponse(
                                dto.getWriterId(),
                                dto.getNickname()
                        ),
                        dto.getContent(),
                        dto.getChildCommentCount(),
                        dto.getCreatedAt(),
                        dto.getModifiedAt()
                )
        );
        return SliceResponse.success("댓글 조회 성공", parentCommentSliceList);
    }

    /**
     * 자식 댓글 조회
     * @param parentCommentId 부모 댓글 id
     * @param postId 게시글 id
     * @param pageable 페이징(slice)
     * @return 자식 조회 결과
     */
    @Transactional(readOnly = true)
    public SliceResponse<CommentChildResponse> getChildComment(Long parentCommentId, Long postId, Pageable pageable) {
        Post post = getPostByIdOrThrow(postId);
        getCommentByIdOrThrow(parentCommentId);

        Slice<CommentChildGetQueryDto> childCommentList = commentRepository.getChildComment(parentCommentId, post.getId(), pageable);

        Slice<CommentChildResponse> childCommentSliceList = childCommentList.map(dto ->
                new CommentChildResponse(
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
        return SliceResponse.success("댓글 조회 성공", childCommentSliceList);
    }
}