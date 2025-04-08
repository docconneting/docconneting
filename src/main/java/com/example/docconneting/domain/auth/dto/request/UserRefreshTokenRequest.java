package com.example.docconneting.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserRefreshTokenRequest {
    @NotBlank(message = "토큰은 필수 입력값 입니다.")
    private String refreshToken;
}
