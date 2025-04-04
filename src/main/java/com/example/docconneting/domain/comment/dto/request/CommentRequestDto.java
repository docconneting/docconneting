package com.example.docconneting.domain.comment.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;


@Getter
@NoArgsConstructor
public class CommentRequestDto {

    @NotBlank(message = "내용은 필수입니다.")
    private String contents;

    public CommentRequestDto(String contents) {
        this.contents = contents;
    }
}
