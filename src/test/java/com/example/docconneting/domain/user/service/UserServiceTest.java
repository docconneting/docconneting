package com.example.docconneting.domain.user.service;

import com.example.docconneting.common.enums.Major;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.domain.auth.entity.AuthUser;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private String image;
    private String majorName;

    private User doctor;
    private User patient;

    private AuthUser authDoctor;
    private AuthUser authPatient;

    @BeforeEach
    void setUp() {
        image = "https://example.com/image.jpg";
        majorName = "INTERNAL_MEDICINE";
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

        authDoctor = AuthUser.of(1L,UserRole.DOCTOR);

        patient = User.of(
                "test2@naver.com",
                "password",
                "patient",
                0,
                false,
                UserRole.PATIENT
        );
        ReflectionTestUtils.setField(patient, "id", 2L);

        authPatient = AuthUser.of(2L,UserRole.PATIENT);
    }

    @Test
    public void 의사_마이페이지_조회_정상(){
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
    public void 환자_마이페이지_조회_정상(){
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
    public void 마이페이지_조회_유저가_없는_경우(){
        // given
        long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        ClientException exception = assertThrows(ClientException.class, () -> userService.findMyPage(authDoctor));
        assertEquals("존재하지 않는 회원입니다.", exception.getErrorCode().getMessage());
    }

    @Test
    public void 의사_이미지_변경_정상(){
        //given

        //when

        //then


    }

    @Test
    public void 비밀번호_변경_정상(){
        //given

        //when

        //then


    }

}
