package com.example.docconneting.domain.user.dto.response;

import lombok.Getter;

@Getter
public class UserMyPageResponse {
    private String username;
    private Integer point;

    // 의사 마이페이지 조회 생성자
    private UserMyPageResponse(String username){
        this.username = username;
    }
    //환자, 관리자 마이페이지 조회 생성자
    private UserMyPageResponse(String username, Integer point) {
        this.username = username;
        this.point = point;
    }

    // 의사 마이페이지 조회 생성 메서드
    public static UserMyPageResponse of(String username){
        return new UserMyPageResponse(username);
    }

    // 환자 마이페이지 조회 생성 메서드
    public static UserMyPageResponse of(String username, Integer point) {
        return new UserMyPageResponse(username, point);
    }
}
