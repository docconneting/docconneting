package com.example.docconneting.domain.Auth.dto.response;


import lombok.Builder;
import lombok.Getter;

@Getter
public class UserRefreshTokenResponseDto {
    private String accessToken;
    private String refreshToken;

    @Builder
    public UserRefreshTokenResponseDto(String accessToken, String refreshToken){
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
