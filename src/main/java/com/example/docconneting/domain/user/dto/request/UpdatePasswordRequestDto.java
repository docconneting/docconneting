package com.example.docconneting.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdatePasswordRequestDto {
    @NotBlank(message = "기존 비밀번호 입력은 필수입니다.")
    private String oldPassword;

    @NotBlank(message = "신규 비밀번호 입력은 필수입니다.")
    private String newPassword;
}
