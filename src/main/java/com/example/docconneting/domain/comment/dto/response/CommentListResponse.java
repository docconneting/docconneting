package com.example.docconneting.domain.comment.dto.response;

import com.example.docconneting.domain.comment.entity.Comment;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CommentListResponse {
    private final Long id;

    private final String contents;

    private final LocalDateTime createdAt;

    private final LocalDateTime modifiedAt;

    private CommentListResponse(Long id, String contents, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.contents = contents;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static List<CommentListResponse> toCommentListResponses(List<Comment> comments){
        return comments.stream().map(comment ->
                            new CommentListResponse(
                                    comment.getId(),
                                    comment.getContents(),
                                    comment.getCreatedAt(),
                                    comment.getModifiedAt())
                )
                .collect(Collectors.toList());
    }
}
