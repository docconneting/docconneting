package com.example.docconneting.domain.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserMyPageResponseDto {
    private String username;
    private Integer point;

    private UserMyPageResponseDto(String username, Integer point) {
        this.username = username;
        this.point = point;
    }

    public static UserMyPageResponseDto of(String username, Integer point) {
        return new UserMyPageResponseDto(username, point);
    }
}
