package com.example.docconneting.domain.auth.dto.response;

import lombok.Getter;

@Getter
public class UserSignInResponseDto {
    private String accessToken;
    private String refreshToken;

    private UserSignInResponseDto(String accessToken, String refreshToken){
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public static UserSignInResponseDto of(String accessToken, String refreshToken){
        return new UserSignInResponseDto(accessToken, refreshToken);
    }
}
