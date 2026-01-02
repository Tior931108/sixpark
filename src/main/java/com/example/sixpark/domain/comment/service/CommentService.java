package com.example.sixpark.domain.comment.service;

import com.example.sixpark.common.excepion.CustomException;
import com.example.sixpark.domain.comment.entity.Comment;
import com.example.sixpark.domain.comment.model.dto.CommentDto;
import com.example.sixpark.domain.comment.model.request.CommentCreateRequest;
import com.example.sixpark.domain.comment.model.response.CommentCreateResponse;
import com.example.sixpark.domain.comment.model.response.WriterResponse;
import com.example.sixpark.domain.comment.repository.CommentRepository;
import com.example.sixpark.domain.post.entity.Post;
import com.example.sixpark.domain.post.reository.PostRepository;
import com.example.sixpark.domain.user.entity.User;
import com.example.sixpark.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.sixpark.common.enums.ErrorMessage.*;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    // 댓글 생성
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

    // 부모 댓글 검증
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

    // 유저 아이디가 일치하는 유저가 없으면 예외처리
    private User getUserByIdOrThrow(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new CustomException(NOT_FOUND_USER)
        );
    }

    // 게시글 아이디가 일치하는 게시글 없으면 예외처리
    private Post getPostByIdOrThrow(Long id) {
        return postRepository.findById(id).orElseThrow(
                () -> new CustomException(NOT_FOUND_TASK) // 나중에 상수를 NOT_FOUND_POST로 변경
        );
    }

    // 부모 댓글이 있으면 
    private Comment getCommentByIdOrThrow(Long parentId) {
        return commentRepository.findById(parentId).orElseThrow(
                () -> new CustomException(NOT_FOUND_COMMENT)
        );
    }
}
