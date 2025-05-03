package com.example.docconneting.domain.auth.controller;

import com.example.docconneting.common.response.Response;
import com.example.docconneting.domain.auth.annotation.Auth;
import com.example.docconneting.domain.auth.dto.request.UserRefreshTokenRequest;
import com.example.docconneting.domain.auth.dto.request.UserSignInRequest;
import com.example.docconneting.domain.auth.dto.request.UserSignUpRequest;
import com.example.docconneting.domain.auth.dto.response.UserRefreshTokenResponse;
import com.example.docconneting.domain.auth.dto.response.UserSignInResponse;
import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthService authService;

    //회원가입
    @PostMapping("/signup")
    public ResponseEntity<Response<Map<String, String>>> signUp(
            @RequestPart(required = false) MultipartFile multipartFile,
            @Valid @RequestPart UserSignUpRequest requestDto
    ) throws IOException {
        Map<String, String> response = authService.signUp(requestDto, multipartFile);
        return ResponseEntity.ok(Response.of(response));
    }

    //로그인
    @PostMapping("/signin")
    public ResponseEntity<Response<UserSignInResponse>> signIn(
            @Valid @RequestBody UserSignInRequest requestDto
    ) {
        UserSignInResponse response = authService.signIn(requestDto);
        authService.saveFcmToken(requestDto);
        return ResponseEntity.ok(Response.of(response));
    }

    //토큰 재발급
    @PostMapping("/refresh")
    public ResponseEntity<Response<UserRefreshTokenResponse>> refreshToken(
            @Auth AuthUser authUser,
            @Valid @RequestBody UserRefreshTokenRequest requestDto
    ) {
        UserRefreshTokenResponse response = authService.refreshAccessToken(authUser, requestDto);

        return ResponseEntity.ok(Response.of(response));
    }
}