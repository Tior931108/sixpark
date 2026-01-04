package com.example.sixpark.domain.comment.model.dto;

import com.example.sixpark.domain.comment.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {

    private Long id;
    private Long postId;
    private Long writerId;
    private Long parentId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static CommentDto from(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getPost().getId(),
                comment.getUser().getId(),
                comment.getParentComment() != null ? comment.getParentComment().getId() : null,
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getModifiedAt()
        );
    }
}