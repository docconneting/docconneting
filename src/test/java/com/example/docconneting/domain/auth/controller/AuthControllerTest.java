package com.example.docconneting.domain.auth.controller;


import com.example.docconneting.domain.auth.dto.request.UserRefreshTokenRequest;
import com.example.docconneting.domain.auth.dto.request.UserSignInRequest;
import com.example.docconneting.domain.auth.dto.request.UserSignUpRequest;
import com.example.docconneting.domain.auth.dto.response.UserRefreshTokenResponse;
import com.example.docconneting.domain.auth.dto.response.UserSignInResponse;
import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.auth.service.AuthService;
import com.example.docconneting.domain.user.enums.UserRole;
import com.example.docconneting.domain.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UserRepository userRepository;

    //JPA, @EntityListeners(AuditingEntityListener.class) 무시용 mock 삽입
    @MockitoBean
    private AuditorAware<String> auditorAware;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    public void 회원가입() throws Exception {
        //given
        UserSignUpRequest request = new UserSignUpRequest();
        ReflectionTestUtils.setField(request, "email", "test@test.com");
        ReflectionTestUtils.setField(request, "password", "test");
        ReflectionTestUtils.setField(request, "username", "testpatient");
        ReflectionTestUtils.setField(request, "userRole", "PATIENT");
        Map<String, String> message = new HashMap<>();
        message.put("message", "회원 가입이 성공적으로 됐습니다");
        given(authService.signUp(any())).willReturn(message);

        //when & then
        mockMvc.perform(post("/api/v1/signup")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.message").value(message.get("message")));

    }

    @Test
    public void 로그인() throws Exception {
        //given
        UserSignInRequest request = new UserSignInRequest();
        ReflectionTestUtils.setField(request, "email", "test@test.com");
        ReflectionTestUtils.setField(request, "password", "test");

        String accessToken = "access";
        String refreshToken = "refresh";

        given(authService.signIn(any())).willReturn(UserSignInResponse.of(accessToken,refreshToken));

        //when & then
        mockMvc.perform(post("/api/v1/signin")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.accessToken").value(accessToken))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.refreshToken").value(refreshToken));
    }

    @Test
    public void accessToken_재발급() throws Exception {
        //given
        AuthUser authUser;
        authUser = AuthUser.of(1L, UserRole.PATIENT);

        UserRefreshTokenRequest request = new UserRefreshTokenRequest();
        ReflectionTestUtils.setField(request, "refreshToken", "refresh");

        String newAccessToken = "new";
        String newRefreshToken = "new";

        given(authService.refreshAccessToken(any(),any())).willReturn(UserRefreshTokenResponse.of(newAccessToken,newRefreshToken));

        //when & then
        mockMvc.perform(post("/api/v1/refresh")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.accessToken").value(newAccessToken))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.refreshToken").value(newRefreshToken));

    }
}
