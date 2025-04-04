package com.example.docconneting.domain.comment.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentRequestDto {

    private String contents;

    public CommentRequestDto(String contents) {
        this.contents = contents;
    }
}
