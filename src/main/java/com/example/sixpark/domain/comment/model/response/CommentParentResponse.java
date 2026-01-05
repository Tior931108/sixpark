package com.example.sixpark.domain.comment.model.response;

import com.example.sixpark.domain.comment.model.dto.CommentDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommentParentResponse {
    private final Long id;
    private final Long postId;
    private final Long writerId;
    private final WriterResponse writer;
    private final String content;
    private final Long childCommentCount;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static CommentParentResponse from(CommentDto dto, Long childCommentCount, WriterResponse writer) {
        return new CommentParentResponse(
                dto.getId(),
                dto.getPostId(),
                dto.getWriterId(),
                writer,
                dto.getContent(),
                childCommentCount,
                dto.getCreatedAt(),
                dto.getModifiedAt()
        );
    }
}