package com.example.docconneting.domain.user.service;

import com.example.docconneting.common.config.PasswordEncoder;
import com.example.docconneting.common.enums.Major;
import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.user.dto.request.UpdateImageRequest;
import com.example.docconneting.domain.user.dto.request.UpdatePasswordRequest;
import com.example.docconneting.domain.user.dto.response.DoctorMyPageResponse;
import com.example.docconneting.domain.user.dto.response.PatientMyPageResponse;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.enums.UserRole;
import com.example.docconneting.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalTime;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User doctor;
    private User patient;

    private AuthUser authDoctor;
    private AuthUser authPatient;

    @BeforeEach
    void setUp() {
        String image = "https://example.com/image.jpg";
        String majorName = "INTERNAL_MEDICINE";
        Major major = Major.of(majorName);

        doctor = User.of("test1@naver.com", "password", "doctor", major, image, LocalTime.of(9, 0), LocalTime.of(18, 0), false, UserRole.DOCTOR);
        ReflectionTestUtils.setField(doctor, "id", 1L);

        authDoctor = AuthUser.of(1L, UserRole.DOCTOR);

        patient = User.of("test2@naver.com", "password", "patient", 0, false, UserRole.PATIENT);
        ReflectionTestUtils.setField(patient, "id", 2L);

        authPatient = AuthUser.of(2L, UserRole.PATIENT);
    }

    @Test
    public void 의사_마이페이지_조회_정상() {
        //given
        long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.of(doctor));

        //when
        DoctorMyPageResponse response = (DoctorMyPageResponse) userService.findMyPage(authDoctor);

        //then
        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("doctor");

    }

    @Test
    public void 환자_마이페이지_조회_정상() {
        //given
        long userId = 2L;
        given(userRepository.findById(userId)).willReturn(Optional.of(patient));

        //when
        PatientMyPageResponse response = (PatientMyPageResponse) userService.findMyPage(authPatient);

        //then
        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("patient");
        assertThat(response.getPoint()).isEqualTo(0);

    }

    @Test
    public void 마이페이지_조회_유저가_없는_경우() {
        // given
        long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        ClientException exception = assertThrows(ClientException.class, () -> userService.findMyPage(authDoctor));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    public void 비밀번호_변경_정상() {
        //given
        long userId = 1L;
        String messageValue = "비밀 번호 수정이 성공적으로 됐습니다";
        String encodedPassword = "new-password";

        given(passwordEncoder.matches(any(), any())).willReturn(true);
        given(userRepository.findById(userId)).willReturn(Optional.of(doctor));
        given(passwordEncoder.encode("new")).willReturn(encodedPassword);

        UpdatePasswordRequest request = new UpdatePasswordRequest();
        ReflectionTestUtils.setField(request, "oldPassword", "old");
        ReflectionTestUtils.setField(request, "newPassword", "new");

        //when
        Map<String, String> response = userService.updatePassword(authDoctor, request);

        //then
        assertThat(response.get("message")).isEqualTo(messageValue);
        assertThat(doctor.getPassword()).isEqualTo(encodedPassword);
    }


    @Test
    public void 비밀번호_변경_기존_비밀번호_오류() {
        // given
        long userId = 1L;
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        ReflectionTestUtils.setField(request, "oldPassword", "old");
        ReflectionTestUtils.setField(request, "newPassword", "new");

        given(userRepository.findById(userId)).willReturn(Optional.of(doctor));
        given(passwordEncoder.matches(any(),any())).willReturn(false);

        // when & then
        ClientException exception = assertThrows(ClientException.class, () ->
                userService.updatePassword(authDoctor, request));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_PASSWORD);
    }


    @Test
    public void 비밀번호_변경_기존과_신규가_동일함(){
        // given
        long userId = 1L;
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        ReflectionTestUtils.setField(request, "oldPassword", "same");
        ReflectionTestUtils.setField(request, "newPassword", "same");

        given(userRepository.findById(userId)).willReturn(Optional.of(doctor));
        given(passwordEncoder.matches(any(),any())).willReturn(true);

        // when & then
        ClientException exception = assertThrows(ClientException.class, () ->
                userService.updatePassword(authDoctor, request));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PASSWORD_SAME_AS_OLD);

    }

    @Test
    public void 의사_이미지_변경_정상() {
        //given
        long userId = 1L;
        String messageValue = "이미지 수정이 성공적으로 됐습니다";
        String newImageUrl = "https://example.com/newimage.jpg";
        given(userRepository.findById(userId)).willReturn(Optional.of(doctor));
        UpdateImageRequest request = new UpdateImageRequest();
        ReflectionTestUtils.setField(request,"newImage",newImageUrl);

        given(userRepository.findById(userId)).willReturn(Optional.of(doctor));

        //when
        Map<String, String> response = userService.updateImage(authDoctor,request);

        //then
        assertThat(response.get("message")).isEqualTo(messageValue);
        assertThat(doctor.getImage()).isEqualTo(newImageUrl);

    }

    @Test
    public void 이미지_변경_환자가_접근(){
        //given
        long userId = 2L;
        UpdateImageRequest request = new UpdateImageRequest();
        ReflectionTestUtils.setField(request,"newImage","https://example.com/newimage.jpg");
        given(userRepository.findById(userId)).willReturn(Optional.of(patient));

        //when & then
        ClientException exception = assertThrows(ClientException.class, () ->
                userService.updateImage(authPatient, request));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.UNAUTHORIZED_USER);
    }

}
