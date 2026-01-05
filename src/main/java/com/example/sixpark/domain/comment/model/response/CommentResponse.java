package com.example.sixpark.domain.comment.model.response;

import com.example.sixpark.domain.comment.model.dto.CommentDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentResponse {
    private final Long id;
    private final Long postId;
    private final Long writerId;
    private final WriterResponse writer;
    private final String content;
    private final Long parentId;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static CommentResponse from(CommentDto dto, WriterResponse writer) {
        return new CommentResponse(
                dto.getId(),
                dto.getPostId(),
                dto.getWriterId(),
                writer,
                dto.getContent(),
                dto.getParentId(),
                dto.getCreatedAt(),
                dto.getModifiedAt()
        );
    }
}
