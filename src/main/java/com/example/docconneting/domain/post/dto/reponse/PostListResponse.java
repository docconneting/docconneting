package com.example.docconneting.domain.post.dto.reponse;

import com.example.docconneting.domain.post.entity.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    private PostListResponse(Long id, String patientName, String title, String contents, String major, Boolean isReplied, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.patientName = patientName;
        this.title = title;
        this.contents = contents;
        this.major = major;
        this.isReplied = isReplied;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static List<PostListResponse> toPostListResponses(List<Post> posts){
        return posts.stream().map(post ->
                        new PostListResponse(
                                post.getId(),
                                post.getPatient().getUsername(),
                                post.getTitle(),
                                post.getContents(),
                                post.getMajor().name(),
                                post.getIsReplied(),
                                post.getCreatedAt(),
                                post.getModifiedAt())
                )
                .collect(Collectors.toList());
    }
}
