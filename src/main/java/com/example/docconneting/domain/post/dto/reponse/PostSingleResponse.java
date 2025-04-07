package com.example.docconneting.domain.post.dto.reponse;

import com.example.docconneting.domain.post.entity.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostSingleResponse {
    private final Long id;

    private final String patientName;

    private final String title;

    private final String contents;

    private final String major;

    private final Boolean isReplied;

    private final LocalDateTime deadline;

    private final LocalDateTime createdAt;

    private final LocalDateTime modifiedAt;

    private PostSingleResponse(Long id, String patientName, String title, String contents, String major, Boolean isReplied, LocalDateTime deadline, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.patientName = patientName;
        this.title = title;
        this.contents = contents;
        this.major = major;
        this.isReplied = isReplied;
        this.deadline = deadline;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static PostSingleResponse of(Long id, String patientName, String title, String contents, String major, Boolean isReplied, LocalDateTime deadline, LocalDateTime createdAt, LocalDateTime modifiedAt){
        return new PostSingleResponse(id, patientName, title, contents, major, isReplied, deadline, createdAt, modifiedAt);
    }
}
