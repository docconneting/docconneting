package com.example.docconneting.domain.Auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserRefreshTokenRequestDto {
    @NotBlank(message = "토큰은 필수 입력값 입니다.")
    private String refreshToken;
}
