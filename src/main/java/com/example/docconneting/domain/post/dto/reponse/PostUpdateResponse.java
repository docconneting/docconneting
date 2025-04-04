package com.example.docconneting.domain.post.dto.reponse;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostUpdateResponse {
    private final Long id;

    private final String title;

    private final String contents;

    private final String major;

    private final LocalDateTime createdAt;

    private final LocalDateTime modifiedAt;

    @Builder
    public PostUpdateResponse(Long id, String title, String contents, String major, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.major = major;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}
