package com.example.docconneting.domain.user.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;

@Getter
public class DoctorMyPageResponse implements UserMyPageResponse{
    private String username;

    //생성자
    private DoctorMyPageResponse(String username){
        this.username = username;
    }

    //생성 메서드
    public static DoctorMyPageResponse of(String username){
        return new DoctorMyPageResponse(username);
    }
}
