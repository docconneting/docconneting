package com.example.docconneting.domain.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PostUpdateRequest {
    @NotBlank(message = "제목은 필수 입력 값 입니다.")
    private String title;

    @NotBlank(message = "내용은 필수 입력 값 입니다.")
    private String contents;
}
