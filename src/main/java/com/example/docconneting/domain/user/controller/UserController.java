package com.example.docconneting.domain.user.controller;

import com.example.docconneting.common.response.Response;
import com.example.docconneting.domain.Auth.annotation.Auth;
import com.example.docconneting.domain.Auth.dto.AuthUser;
import com.example.docconneting.domain.user.dto.request.UpdateImageRequestDto;
import com.example.docconneting.domain.user.dto.request.UpdatePasswordRequestDto;
import com.example.docconneting.domain.user.dto.response.UserMyPageResponseDto;
import com.example.docconneting.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    //마이페이지 조회
    @GetMapping
    public ResponseEntity<Response<UserMyPageResponseDto>> findMyPage(
            @Auth AuthUser authUser
    ) {
        UserMyPageResponseDto response = userService.findMyPage(authUser);
        return ResponseEntity.ok(Response.of(response));
    }

    //비밀번호 수정
    @PatchMapping("/password")
    public ResponseEntity<Response<Map<String, String>>> updatePassword(
            @Auth AuthUser authUser,
            @Valid @RequestBody UpdatePasswordRequestDto dto
    ) {
        Map<String, String> response = userService.updatePassword(authUser, dto);
        return ResponseEntity.ok(Response.of(response));
    }

    //의사 프로필 이미지 수정 (의사만 가능)
    @PatchMapping("/profile")
    public ResponseEntity<Response<Map<String, String>>> updateImage(
            @Auth AuthUser authUser,
            @Valid @RequestBody UpdateImageRequestDto dto
    ) {
        Map<String, String> response = userService.updateImage(authUser, dto);
        return ResponseEntity.ok(Response.of(response));
    }

}
