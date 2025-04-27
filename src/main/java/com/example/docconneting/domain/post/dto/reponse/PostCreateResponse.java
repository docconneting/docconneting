package com.example.docconneting.domain.post.dto.reponse;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostCreateResponse {

    private final Long id;
    private final String title;
    private final String contents;
    private final String major;
    private final LocalDateTime createdAt;

    private PostCreateResponse(Long id, String title, String contents, String major, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.major = major;
        this.createdAt = createdAt;
    }

    public static PostCreateResponse of(Long id, String title, String contents, String major, LocalDateTime createdAt) {
        return new PostCreateResponse(id, title, contents, major, createdAt);
    }
}
