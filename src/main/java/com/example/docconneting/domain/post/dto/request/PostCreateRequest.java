package com.example.docconneting.domain.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostCreateRequest {

    @NotBlank(message = "제목은 필수 값입니다.")
    private String title;

    @NotBlank(message = "내용은 필수 값입니다.")
    private String contents;

    @NotBlank(message = "전공은 필수 값입니다.")
    private String major;

    @NotBlank(message = "결제 타입은 필수 값입니다.")
    private String payType;
}
