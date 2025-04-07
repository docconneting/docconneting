package com.example.docconneting.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateImageRequestDto {
    @NotBlank(message = "신규 이미지는 필수사항입니다.")
    private String newImage;
}
