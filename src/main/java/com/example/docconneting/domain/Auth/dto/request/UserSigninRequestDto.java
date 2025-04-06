package com.example.docconneting.domain.Auth.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UserSigninRequestDto {
    @NotNull(message = "이메일은 필수 입력값입니다.")
    private String email;

    @NotNull(message = "비밀번호는 필수 입력값입니다.")
    private String password;
}
