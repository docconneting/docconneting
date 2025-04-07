package com.example.docconneting.domain.Auth.controller;

import com.example.docconneting.common.response.Response;
import com.example.docconneting.domain.Auth.annotation.Auth;
import com.example.docconneting.domain.Auth.dto.AuthUser;
import com.example.docconneting.domain.Auth.service.AuthService;
import com.example.docconneting.domain.Auth.dto.request.UserRefreshTokenRequestDto;
import com.example.docconneting.domain.Auth.dto.request.UserSignUpRequestDto;
import com.example.docconneting.domain.Auth.dto.request.UserSigninRequestDto;
import com.example.docconneting.domain.Auth.dto.response.UserRefreshTokenResponseDto;
import com.example.docconneting.domain.Auth.dto.response.UserSignInResponseDto;
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
            @Valid @RequestBody UserSignUpRequestDto requestDto
    ) {
        Response<Map<String, String>> response = authService.signUp(requestDto);

        return ResponseEntity.ok(response);
    }

    //로그인
    @PostMapping("/signin")
    public ResponseEntity<Response<UserSignInResponseDto>> signIn(
            @Valid @RequestBody UserSigninRequestDto requestDto
    ) {
        UserSignInResponseDto response = authService.signIn(requestDto);

        return ResponseEntity.ok(Response.of(response));
    }

    //토큰 재발급
    @PostMapping("/refresh")
    public ResponseEntity<Response<UserRefreshTokenResponseDto>> refreshToken(
            @Auth AuthUser authUser,
            @Valid @RequestBody UserRefreshTokenRequestDto requestDto
    ) {
        UserRefreshTokenResponseDto response = authService.refreshToken(authUser, requestDto);

        return ResponseEntity.ok(Response.of(response));
    }
}