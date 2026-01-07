package com.example.sixpark.domain.comment.service;

import com.example.sixpark.common.enums.ErrorMessage;
import com.example.sixpark.common.excepion.CustomException;
import com.example.sixpark.domain.comment.entity.Comment;
import com.example.sixpark.domain.comment.model.dto.CommentChildGetQueryDto;
import com.example.sixpark.domain.comment.model.dto.CommentDto;
import com.example.sixpark.domain.comment.model.dto.CommentParentGetQueryDto;
import com.example.sixpark.domain.comment.model.request.CommentCreateRequest;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    /**
     * 댓글 생성
     * @param userId  로그인한 유저의 아이디
     * @param request 댓글 생성 요청 DTO
     *                - parentId가 null이면 일반 댓글 생성
     *                - parentId가 있으면 대댓글 생성
     * @return 댓글 생성 결과
     */
    @Transactional
    public CommentResponse createComment(Long userId, CommentCreateRequest request) {
        User writer = getUserByIdOrThrow(userId);
        Post post = getPostByIdOrThrow(request.getPostId());

        Comment parent = null;
        if (request.getParentId() != null) {
            parent = getCommentByIdOrThrow(request.getParentId());
            invalidParentComment(parent, post);
            parent.addChildComments();
        }

        Comment comment = new Comment(request.getContent(), post, writer, parent);

        commentRepository.save(comment);

        return CommentResponse.from(CommentDto.from(comment), WriterResponse.from(UserDto.from(writer)));
    }

    /**
     * 댓글 수정
     * @param userId    로그인한 유저의 아이디
     * @param commentId 댓글 아이디
     * @param request   댓글 수정 요청 DTO
     * @return 댓글 수정 결과
     */
    @Transactional
    public CommentResponse updateComment(Long userId, Long commentId, CommentUpdateRequest request) {
        User writer = getUserByIdOrThrow(userId);
        Comment comment = getCommentByIdOrThrow(commentId);

        matchedWriter(writer.getId(), comment.getUser().getId());

        comment.update(request.getContent());
        commentRepository.save(comment);

        return CommentResponse.from(CommentDto.from(comment), WriterResponse.from(UserDto.from(writer)));
    }

    /**
     * 댓글 삭제
     * @param userId    유저 아이디
     * @param commentId 댓글 아이디
     */
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        User writer = getUserByIdOrThrow(userId);
        Comment comment = getCommentByIdOrThrow(commentId);

        matchedWriter(writer.getId(), comment.getUser().getId());

        // 자식 댓글인 경우 삭제 로직 return이 없으면 전체삭제됨
        if (comment.getParentComment() != null) {
            Comment parent = comment.getParentComment();
            parent.minusChildComments();
            comment.softDelete();
            return;
        }

        deleteChildComments(comment);
        comment.softDelete();
    }

    /**
     * 댓글 검색
     * @param postId    게시글 id
     * @param searchKey 검색어
     * @param pageable  페이징
     * @return 댓글 검색 결과
     */
    @Transactional(readOnly = true)
    public Slice<CommentParentResponse> getSearchComment(Long postId, String searchKey, Pageable pageable) {
        Post post = getPostByIdOrThrow(postId);

        Slice<CommentParentGetQueryDto> commentList = commentRepository.getSearchComments(post.getId(), searchKey, pageable);

        return commentList.map(dto -> new CommentParentResponse(dto.getId(), dto.getPostId(), dto.getWriterId(), new WriterResponse(dto.getWriterId(), dto.getNickname()), dto.getContent(), dto.getChildCommentCount(), dto.getCreatedAt(), dto.getModifiedAt()));
    }

    /**
     * 부모 댓글 조회
     * @param postId   게시글 id
     * @param pageable 페이징(slice)
     * @return 부모 조회 결과
     */
    @Transactional(readOnly = true)
    public Slice<CommentParentResponse> getParentComment(Long postId, Pageable pageable) {
        Post post = getPostByIdOrThrow(postId);

        Slice<CommentParentGetQueryDto> parentCommentList = commentRepository.getParentComment(post.getId(), pageable);

        return parentCommentList.map(dto -> new CommentParentResponse(dto.getId(), dto.getPostId(), dto.getWriterId(), new WriterResponse(dto.getWriterId(), dto.getNickname()), dto.getContent(), dto.getChildCommentCount(), dto.getCreatedAt(), dto.getModifiedAt()));
    }

    /**
     * 자식 댓글 조회
     * @param parentCommentId 부모 댓글 id
     * @param postId          게시글 id
     * @param pageable        페이징(slice)
     * @return 자식 조회 결과
     */
    @Transactional(readOnly = true)
    public Slice<CommentChildResponse> getChildComment(Long parentCommentId, Long postId, Pageable pageable) {
        Post post = getPostByIdOrThrow(postId);

        getCommentByIdOrThrow(parentCommentId);

        Slice<CommentChildGetQueryDto> childCommentList = commentRepository.getChildComment(parentCommentId, post.getId(), pageable);

        return childCommentList.map(dto -> new CommentChildResponse(dto.getId(), dto.getPostId(), dto.getWriterId(), new WriterResponse(dto.getWriterId(), dto.getNickname()), dto.getContent(), dto.getParentId(), dto.getCreatedAt(), dto.getModifiedAt()));
    }

    /**
     * 부모 댓글 검증
     * @param parent 부모 댓글
     * @param post   댓글 작성 게시글
     */
    private static void invalidParentComment(Comment parent, Post post) {
        // 해당 게시글에 부모 댓글이 없으면 예외처리 발생
        if (!parent.getPost().getId().equals(post.getId())) {
            throw new CustomException(ErrorMessage.NOT_CORRECT_PARAMETER);
        }
        // 부모 댓글이 삭제되어 있으면 생성안됨
        if (parent.isDeleted()) {
            throw new CustomException(ErrorMessage.NOT_CORRECT_PARAMETER);
        }
        // 이미 부모 댓글이 있으면 대대댓글은 쓸수없도록 즉 대댓글까지만 가능하도록 예외처리(-> 깊이 1)
        if (parent.getParentComment() != null) {
            throw new CustomException(ErrorMessage.NOT_CORRECT_PARAMETER);
        }
    }

    /**
     * 유저 아이디에 해당하는 유저 조회 없으면 예외 발생
     * @param userId 유저 아이디
     * @return 조회된 유저 엔티티
     */
    private User getUserByIdOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorMessage.NOT_FOUND_USER));
    }

    /**
     * 게시글 아이디에 해당하는 게시글 조회 없으면 예외 발생
     * @param postId 게시글 아이디
     * @return 조회된 게시글 엔티티
     */
    private Post getPostByIdOrThrow(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorMessage.NOT_FOUND_POST));
    }

    /**
     * 댓글 아이디에 해당하는 댓글 조회 없으면 예외 발생
     * @param commentId 댓글 아이디
     * @return 조회된 댓글 엔티티
     */
    private Comment getCommentByIdOrThrow(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorMessage.NOT_FOUND_COMMENT));
    }

    /**
     * 작성자가 일치하지 않으면 예외 발생
     * @param userId        유저 아이디
     * @param commentUserId 댓글작성자 아이디
     */
    private static void matchedWriter(Long userId, Long commentUserId) {
        if (!userId.equals(commentUserId)) {
            throw new CustomException(ErrorMessage.NOT_MODIFY_AUTHORIZED);
        }
    }

    /**
     * 부모댓글 사라질때 자식 댓글 삭제 모든 자식 댓글 삭제
     * @param parent 부모 댓글
     */
    private void deleteChildComments(Comment parent) {
        List<Comment> childComments = commentRepository.findByParentComment_Id(parent.getId());
        childComments.forEach(Comment::softDelete);
    }
}