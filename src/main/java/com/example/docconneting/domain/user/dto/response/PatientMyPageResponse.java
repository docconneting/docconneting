package com.example.docconneting.domain.user.dto.response;

import lombok.Getter;

@Getter
public class PatientMyPageResponse implements UserMyPageResponse {
    private String username;
    private int point;

    //생성자
    private PatientMyPageResponse(String username, int point) {
        this.username = username;
        this.point = point;
    }

    //생성 메서드
    public static PatientMyPageResponse of(String username, int point){
        return new PatientMyPageResponse(username,point);
    }
}
