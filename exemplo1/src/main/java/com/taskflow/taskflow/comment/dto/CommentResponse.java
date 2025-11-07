package com.taskflow.taskflow.comment.dto;

import com.taskflow.taskflow.comment.model.Comment;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record CommentResponse(
        UUID id,
        String content,
        UUID taskId,
        UUID authorId,
        Instant createdAt
) {
    public static CommentResponse fromEntity(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .taskId(comment.getTask().getId())
                .authorId(comment.getAuthor().getId())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
