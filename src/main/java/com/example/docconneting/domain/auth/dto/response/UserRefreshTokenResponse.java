package com.example.docconneting.domain.auth.dto.response;


import lombok.Getter;

@Getter
public class UserRefreshTokenResponse {
    private String accessToken;
    private String refreshToken;

    private UserRefreshTokenResponse(String accessToken, String refreshToken){
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public static UserRefreshTokenResponse of(String accessToken, String refreshToken){
        return new UserRefreshTokenResponse(accessToken,refreshToken);
    }
}
