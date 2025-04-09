package com.example.docconneting.domain.auth.service;


import com.example.docconneting.common.config.JwtUtil;
import com.example.docconneting.common.config.PasswordEncoder;
import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.domain.auth.dto.request.UserRefreshTokenRequest;
import com.example.docconneting.domain.auth.dto.request.UserSignUpRequest;
import com.example.docconneting.domain.auth.dto.request.UserSignInRequest;
import com.example.docconneting.domain.auth.dto.response.UserRefreshTokenResponse;
import com.example.docconneting.domain.auth.dto.response.UserSignInResponse;
import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.enums.UserRole;
import com.example.docconneting.domain.user.repository.UserRepository;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthService authService;


    @Test
    public void 환자_회원가입() {
        //given
        UserSignUpRequest request = new UserSignUpRequest();
        ReflectionTestUtils.setField(request, "email", "test@test.com");
        ReflectionTestUtils.setField(request, "password", "test");
        ReflectionTestUtils.setField(request, "username", "testpatient");
        ReflectionTestUtils.setField(request, "userRole", "PATIENT");

        given(passwordEncoder.encode("test")).willReturn("test");

        //when
        Map<String, String> response = authService.signUp(request);

        //then
        assertEquals("회원 가입이 성공적으로 됐습니다", response.get("message"));

    }

    @Test
    public void 의사_회원가입() {
        //given
        UserSignUpRequest request = new UserSignUpRequest();
        ReflectionTestUtils.setField(request, "email", "test@test.com");
        ReflectionTestUtils.setField(request, "password", "test");
        ReflectionTestUtils.setField(request, "username", "testdoctor");
        ReflectionTestUtils.setField(request, "userRole", "DOCTOR");
        ReflectionTestUtils.setField(request, "major", "INTERNAL_MEDICINE");
        ReflectionTestUtils.setField(request, "image", "https://example.com/image.jpg");
        ReflectionTestUtils.setField(request, "startTime", LocalTime.of(9, 0));
        ReflectionTestUtils.setField(request, "endTime", LocalTime.of(21, 0));

        given(passwordEncoder.encode("test")).willReturn("test");

        //when
        Map<String, String> response = authService.signUp(request);

        //then
        assertEquals("회원 가입이 성공적으로 됐습니다", response.get("message"));
    }

    @Test
    public void 이미_존재하는_이메일로_회원가입() {
        //given
        UserSignUpRequest request = new UserSignUpRequest();
        ReflectionTestUtils.setField(request, "email", "test@test.com");
        ReflectionTestUtils.setField(request, "password", "test");
        ReflectionTestUtils.setField(request, "username", "testpatient");
        ReflectionTestUtils.setField(request, "userRole", "PATIENT");
        given(userRepository.findByEmail("test@test.com")).willReturn(Optional.of(mock(User.class)));

        // when & then
        ClientException exception = assertThrows(ClientException.class, () -> authService.signUp(request));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.EMAIL_ALREADY_EXISTS);
    }

    @Test
    public void 회원가입_의사_전공이_null() {
        //given
        UserSignUpRequest request = new UserSignUpRequest();
        ReflectionTestUtils.setField(request, "email", "test@test.com");
        ReflectionTestUtils.setField(request, "password", "test");
        ReflectionTestUtils.setField(request, "username", "testdoctor");
        ReflectionTestUtils.setField(request, "userRole", "doctor");
        ReflectionTestUtils.setField(request, "image", "https://example.com/image.jpg");
        ReflectionTestUtils.setField(request, "startTime", LocalTime.of(9, 0));
        ReflectionTestUtils.setField(request, "endTime", LocalTime.of(21, 0));

        given(passwordEncoder.encode("test")).willReturn("test");
        // when & then
        ClientException exception = assertThrows(ClientException.class, () -> authService.signUp(request));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.MAJOR_NOT_FOUND);

    }

    @Test
    public void 회원가입_의사_이미지가_null() {
        //given
        UserSignUpRequest request = new UserSignUpRequest();
        ReflectionTestUtils.setField(request, "email", "test@test.com");
        ReflectionTestUtils.setField(request, "password", "test");
        ReflectionTestUtils.setField(request, "username", "testdoctor");
        ReflectionTestUtils.setField(request, "userRole", "doctor");
        ReflectionTestUtils.setField(request, "major", "INTERNAL_MEDICINE");
        ReflectionTestUtils.setField(request, "startTime", LocalTime.of(9, 0));
        ReflectionTestUtils.setField(request, "endTime", LocalTime.of(21, 0));

        given(passwordEncoder.encode("test")).willReturn("test");

        // when & then
        ClientException exception = assertThrows(ClientException.class, () -> authService.signUp(request));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.IMAGE_NOT_FOUND);

    }

    @Test
    public void 회원가입_의사_시작시간이_null() {
        //given
        UserSignUpRequest request = new UserSignUpRequest();
        ReflectionTestUtils.setField(request, "email", "test@test.com");
        ReflectionTestUtils.setField(request, "password", "test");
        ReflectionTestUtils.setField(request, "username", "testdoctor");
        ReflectionTestUtils.setField(request, "userRole", "doctor");
        ReflectionTestUtils.setField(request, "major", "INTERNAL_MEDICINE");
        ReflectionTestUtils.setField(request, "image", "https://example.com/image.jpg");
        ReflectionTestUtils.setField(request, "endTime", LocalTime.of(21, 0));

        given(passwordEncoder.encode("test")).willReturn("test");

        // when & then
        ClientException exception = assertThrows(ClientException.class, () -> authService.signUp(request));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.STARTTIME_NOT_FOUND);

    }

    @Test
    public void 회원가입_의사_종료시간이_null() {
        //given
        UserSignUpRequest request = new UserSignUpRequest();
        ReflectionTestUtils.setField(request, "email", "test@test.com");
        ReflectionTestUtils.setField(request, "password", "test");
        ReflectionTestUtils.setField(request, "username", "testdoctor");
        ReflectionTestUtils.setField(request, "userRole", "doctor");
        ReflectionTestUtils.setField(request, "major", "INTERNAL_MEDICINE");
        ReflectionTestUtils.setField(request, "image", "https://example.com/image.jpg");
        ReflectionTestUtils.setField(request, "startTime", LocalTime.of(9, 0));

        given(passwordEncoder.encode("test")).willReturn("test");

        // when & then
        ClientException exception = assertThrows(ClientException.class, () -> authService.signUp(request));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ENDTIME_NOT_FOUND);

    }

    @Test
    public void 로그인() {
        //given
        User user = new User();
        ReflectionTestUtils.setField(user, "email", "test@test.com");
        ReflectionTestUtils.setField(user, "password", "test");
        ReflectionTestUtils.setField(user, "username", "testpatient");
        ReflectionTestUtils.setField(user, "userRole", UserRole.PATIENT);
        ReflectionTestUtils.setField(user, "id", 1L);

        UserSignInRequest request = new UserSignInRequest();
        ReflectionTestUtils.setField(request, "email", "test@test.com");
        ReflectionTestUtils.setField(request, "password", "test");

        String accessToken = "access";
        String refreshToken = "refresh";
        given(userRepository.findByEmail("test@test.com")).willReturn(Optional.of(user));
        given(jwtUtil.createToken(1L, user.getUserRole())).willReturn(accessToken);
        given(jwtUtil.createRefreshToken(1L)).willReturn(refreshToken);
        given(passwordEncoder.matches(any(), any())).willReturn(true);

        //when
        UserSignInResponse response = authService.signIn(request);

        //then
        assertEquals(accessToken, response.getAccessToken());
        assertEquals(refreshToken, response.getRefreshToken());
    }

    @Test
    public void 존재하지_않는_유저로_로그인() {
        //given
        UserSignInRequest request = new UserSignInRequest();
        ReflectionTestUtils.setField(request, "email", "test@test.com");
        ReflectionTestUtils.setField(request, "password", "test");

        given(userRepository.findByEmail(request.getEmail())).willReturn(Optional.empty());

        //when & then
        ClientException exception = assertThrows(ClientException.class, () -> authService.signIn(request));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    public void 로그인_비밀번호가_일치하지_않음() {
        //given
        User user = new User();
        ReflectionTestUtils.setField(user, "email", "test@test.com");
        ReflectionTestUtils.setField(user, "password", "test");
        ReflectionTestUtils.setField(user, "username", "testpatient");
        ReflectionTestUtils.setField(user, "userRole", UserRole.PATIENT);
        ReflectionTestUtils.setField(user, "id", 1L);

        UserSignInRequest request = new UserSignInRequest();
        ReflectionTestUtils.setField(request, "email", "test@test.com");
        ReflectionTestUtils.setField(request, "password", "error");

        given(userRepository.findByEmail(request.getEmail())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(request.getPassword(), user.getPassword())).willReturn(false);

        //when & then
        ClientException exception = assertThrows(ClientException.class, () -> authService.signIn(request));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_PASSWORD);

    }

    @Test
    public void 토큰_재발급() {
        //given
        User user = new User();
        ReflectionTestUtils.setField(user, "email", "test@test.com");
        ReflectionTestUtils.setField(user, "password", "test");
        ReflectionTestUtils.setField(user, "username", "testpatient");
        ReflectionTestUtils.setField(user, "userRole", UserRole.PATIENT);
        ReflectionTestUtils.setField(user, "id", 1L);

        AuthUser authUser;
        authUser = AuthUser.of(1L, UserRole.PATIENT);

        UserRefreshTokenRequest request = new UserRefreshTokenRequest();
        ReflectionTestUtils.setField(request, "refreshToken", "refresh");

        String savedToken = "refresh";
        Long ttl = 100000L;

        String newAccessToken = "new";
        String newRefreshToken = "new";

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(refreshTokenService.getRefreshToken(1L)).willReturn(savedToken);
        given(refreshTokenService.getRefreshTokenTTL(1L)).willReturn(ttl);
        given(jwtUtil.createToken(1L, UserRole.PATIENT)).willReturn(newAccessToken);
        given(jwtUtil.createRefreshToken(1L)).willReturn(newRefreshToken);

        //when
        UserRefreshTokenResponse response = authService.refreshAccessToken(authUser, request);

        //then
        assertEquals(newAccessToken, response.getAccessToken());
        assertEquals(newRefreshToken, response.getRefreshToken());
    }

    @Test
    public void 재발급_리프레시_토큰_만료() {
        //given
        User user = new User();
        ReflectionTestUtils.setField(user, "email", "test@test.com");
        ReflectionTestUtils.setField(user, "password", "test");
        ReflectionTestUtils.setField(user, "username", "testpatient");
        ReflectionTestUtils.setField(user, "userRole", UserRole.PATIENT);
        ReflectionTestUtils.setField(user, "id", 1L);

        AuthUser authUser;
        authUser = AuthUser.of(1L, UserRole.PATIENT);

        UserRefreshTokenRequest request = new UserRefreshTokenRequest();
        ReflectionTestUtils.setField(request, "refreshToken", "refresh");

        String savedToken = "refresh";
        Long ttl = 0L;

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(refreshTokenService.getRefreshToken(1L)).willReturn(savedToken);
        given(refreshTokenService.getRefreshTokenTTL(1L)).willReturn(ttl);

        //when & then
        ClientException exception = assertThrows(ClientException.class, () -> authService.refreshAccessToken(authUser,request));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.EXPIRED_REFRESH_TOKEN);

    }

    @Test
    public void 재발급_리프레시_토큰_불일치() {
        //given
        User user = new User();
        ReflectionTestUtils.setField(user, "email", "test@test.com");
        ReflectionTestUtils.setField(user, "password", "test");
        ReflectionTestUtils.setField(user, "username", "testpatient");
        ReflectionTestUtils.setField(user, "userRole", UserRole.PATIENT);
        ReflectionTestUtils.setField(user, "id", 1L);

        AuthUser authUser;
        authUser = AuthUser.of(1L, UserRole.PATIENT);

        UserRefreshTokenRequest request = new UserRefreshTokenRequest();
        ReflectionTestUtils.setField(request, "refreshToken", "wrong");

        String savedToken = "refresh";
        Long ttl = 100000L;

        String newAccessToken = "new";
        String newRefreshToken = "new";

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(refreshTokenService.getRefreshToken(1L)).willReturn(savedToken);
        given(refreshTokenService.getRefreshTokenTTL(1L)).willReturn(ttl);

        //when && then
        ClientException exception = assertThrows(ClientException.class, () -> authService.refreshAccessToken(authUser,request));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_REFRESH_TOKEN);
    }

}
