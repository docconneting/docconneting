package com.example.docconneting.domain.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserMyPageResponseDto {
    private String username;
    private Integer point;

    @Builder
    public UserMyPageResponseDto(String username, Integer point)
    {
        this.username = username;
        this.point = point;
    }
}
