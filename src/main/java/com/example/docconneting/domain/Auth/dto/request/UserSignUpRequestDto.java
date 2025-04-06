package com.example.docconneting.domain.Auth.dto.request;

import com.example.docconneting.common.enums.Major;
import com.example.docconneting.domain.user.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalTime;

@Getter
public class UserSignUpRequestDto {
    @Email
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    private String password;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String username;

    @NotNull(message = "역할은 필수 입력 값입니다.")
    private String userRole; // DOCTOR or PATIENT or ADMIN

    // 의사 전용 필드
    private String major;

    private String image;

    private LocalTime startTime;

    private LocalTime endTime;
}
