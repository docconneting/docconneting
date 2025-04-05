package com.example.docconneting.domain.post.dto.reponse;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostListResponse {

    private final Long id;

    private final String patientName;

    private final String title;

    private final String contents;

    private final String major;

    private final Boolean isReplied;

    private final LocalDateTime createdAt;

    private final LocalDateTime modifiedAt;

    @Builder
    public PostListResponse(Long id, String patientName, String title, String contents, String major, Boolean isReplied, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.patientName = patientName;
        this.title = title;
        this.contents = contents;
        this.major = major;
        this.isReplied = isReplied;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}
