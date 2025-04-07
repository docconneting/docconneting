package com.example.docconneting.domain.auth.dto.response;


import lombok.Getter;

@Getter
public class UserRefreshTokenResponseDto {
    private String accessToken;
    private String refreshToken;

    private UserRefreshTokenResponseDto(String accessToken, String refreshToken){
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public static UserRefreshTokenResponseDto of(String accessToken, String refreshToken){
        return new UserRefreshTokenResponseDto(accessToken,refreshToken);
    }
}
