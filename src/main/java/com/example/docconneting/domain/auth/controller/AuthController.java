package com.example.docconneting.domain.auth.controller;

import com.example.docconneting.common.response.Response;
import com.example.docconneting.domain.auth.annotation.Auth;
import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.auth.service.AuthService;
import com.example.docconneting.domain.auth.dto.request.UserRefreshTokenRequest;
import com.example.docconneting.domain.auth.dto.request.UserSignUpRequest;
import com.example.docconneting.domain.auth.dto.request.UserSigninRequest;
import com.example.docconneting.domain.auth.dto.response.UserRefreshTokenResponseDto;
import com.example.docconneting.domain.auth.dto.response.UserSignInResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthService authService;

    //회원가입
    @PostMapping("/signup")
    public ResponseEntity<Response<Map<String, String>>> signUp(
            @Valid @RequestBody UserSignUpRequest requestDto
    ) {
        Map<String, String> response = authService.signUp(requestDto);

        return ResponseEntity.ok(Response.of(response));
    }

    //로그인
    @PostMapping("/signin")
    public ResponseEntity<Response<UserSignInResponseDto>> signIn(
            @Valid @RequestBody UserSigninRequest requestDto
    ) {
        UserSignInResponseDto response = authService.signIn(requestDto);

        return ResponseEntity.ok(Response.of(response));
    }

    //토큰 재발급
    @PostMapping("/refresh")
    public ResponseEntity<Response<UserRefreshTokenResponseDto>> refreshToken(
            @Auth AuthUser authUser,
            @Valid @RequestBody UserRefreshTokenRequest requestDto
    ) {
        UserRefreshTokenResponseDto response = authService.refreshAccessToken(authUser, requestDto);

        return ResponseEntity.ok(Response.of(response));
    }
}