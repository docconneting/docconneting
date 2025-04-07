package com.example.docconneting.domain.comment.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponseDto {

    private final Long id;
    private final String contents;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private CommentResponseDto(Long id, String contents, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.contents = contents;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static CommentResponseDto of(Long id, String contents, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new CommentResponseDto(id, contents, createdAt, updatedAt);
    }
}
