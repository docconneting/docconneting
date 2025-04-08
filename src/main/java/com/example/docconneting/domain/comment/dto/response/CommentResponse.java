package com.example.docconneting.domain.comment.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponse {

    private final Long id;
    private final String contents;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private CommentResponse(Long id, String contents, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.contents = contents;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static CommentResponse of(Long id, String contents, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new CommentResponse(id, contents, createdAt, updatedAt);
    }
}
