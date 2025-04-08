package com.example.docconneting.domain.auth.dto.response;

import lombok.Getter;

@Getter
public class UserSignInResponse {
    private String accessToken;
    private String refreshToken;

    private UserSignInResponse(String accessToken, String refreshToken){
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public static UserSignInResponse of(String accessToken, String refreshToken){
        return new UserSignInResponse(accessToken, refreshToken);
    }
}
