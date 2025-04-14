package com.example.docconneting.domain.user.controller;

import com.example.docconneting.common.config.JwtUtil;
import com.example.docconneting.common.enums.Major;
import com.example.docconneting.common.resolver.AuthUserArgumentResolver;
import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.user.dto.request.UpdatePasswordRequest;
import com.example.docconneting.domain.user.dto.response.DoctorMyPageResponse;
import com.example.docconneting.domain.user.dto.response.PatientMyPageResponse;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.enums.UserRole;
import com.example.docconneting.domain.user.service.S3Service;
import com.example.docconneting.domain.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AuthUserArgumentResolver authUserArgumentResolver;
    
    @MockitoBean
    private S3Service s3Service;

    //JPA, @EntityListeners(AuditingEntityListener.class) 무시용 mock 삽입
    @MockitoBean
    private AuditorAware<String> auditorAware;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;


    private User doctor;
    private User patient;

    private AuthUser authDoctor;
    private AuthUser authPatient;

    @BeforeEach
    void setUp() {
        String image = "https://example.com/image.jpg";
        String majorName = "INTERNAL_MEDICINE";
        Major major = Major.of(majorName);

        doctor = User.of(
                "test1@naver.com",
                "password",
                "doctor",
                major,
                image,
                LocalTime.of(9, 0),
                LocalTime.of(18, 0),
                false,
                UserRole.DOCTOR
        );
        ReflectionTestUtils.setField(doctor, "id", 1L);

        authDoctor = AuthUser.of(1L, UserRole.DOCTOR);

        patient = User.of(
                "test2@naver.com",
                "password",
                "patient",
                0,
                false,
                UserRole.PATIENT
        );
        ReflectionTestUtils.setField(patient, "id", 2L);

        authPatient = AuthUser.of(2L, UserRole.PATIENT);
    }

    @Test
    void 의사_마이페이지_조회() throws Exception {
        //given
        long userId = 1L;
        String username = "doctor";
        String accessToken = "token";
        DoctorMyPageResponse response = DoctorMyPageResponse.of(username);
        given(authUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(authDoctor);
        given(jwtUtil.createToken(authDoctor.getId(), authDoctor.getUserRole())).willReturn(accessToken);

        given(userService.findMyPage(any())).willReturn(response);

        //when & then
        mockMvc.perform(get("/api/v1/users")
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.username").value(username));
    }

    @Test
    void 환자_마이페이지_조회() throws Exception {
        //given
        long userId = 2L;
        String username = "patient";
        int point = 0;
        String accessToken = "token";
        given(jwtUtil.createToken(userId, UserRole.PATIENT)).willReturn(accessToken);
        given(userService.findMyPage(any(AuthUser.class))).willReturn(PatientMyPageResponse.of(username, point));
        given(authUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(authPatient);

        //when & then
        mockMvc.perform(get("/api/v1/users").header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.username").value(username))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.point").value(point));
    }

    @Test
    void 비밀번호_수정() throws Exception {
        //given
        long userId = 1L;
        String username = "doctor";
        String accessToken = "token";
        String messageValue = "비밀 번호 수정이 성공적으로 됐습니다";
        Map<String, String> message = new HashMap<>();
        message.put("message", messageValue);
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        ReflectionTestUtils.setField(request, "oldPassword", "old");
        ReflectionTestUtils.setField(request, "newPassword", "new");


        given(jwtUtil.createToken(userId, UserRole.DOCTOR)).willReturn(accessToken);
        given(userService.updatePassword(any(), refEq(request))).willReturn(message);
        given(authUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(authDoctor);


        //when&then
        mockMvc.perform(
                        patch("/api/v1/users/password")
                                .header("Authorization", accessToken)
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request)) //json으로 직렬화
                )
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.message").value(messageValue));

    }

    @Test
    void 의사_이미지_수정() throws Exception {
        // given
        long userId = 1L;
        String accessToken = "token";
        String messageValue = "이미지 수정이 성공적으로 됐습니다";

        MockMultipartFile newImage = new MockMultipartFile(
                "multipartFile",
                "img.jpg",
                "image/jpeg",
                "fake image content".getBytes()
        );

        Map<String, String> message = new HashMap<>();
        message.put("message", messageValue);

        String newImageUrl = "https://example.com/newimage.jpg";

        given(jwtUtil.createToken(userId, UserRole.DOCTOR)).willReturn(accessToken);
        given(s3Service.updateImage(any(Long.class),any(String.class), refEq(newImage))).willReturn(newImageUrl);
        given(userService.updateImage(any(), refEq(newImage))).willReturn(message);
        given(authUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(authDoctor);

        // when & then
        mockMvc.perform(multipart("/api/v1/users/profile")
                        .file(newImage)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        })
                        .contentType("multipart/form-data")
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.message").value(messageValue));
    }

}
