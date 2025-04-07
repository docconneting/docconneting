package com.example.docconneting.domain.Auth.service;

import com.example.docconneting.common.config.JwtUtil;
import com.example.docconneting.common.config.PasswordEncoder;
import com.example.docconneting.common.enums.Major;
import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.common.response.Response;
import com.example.docconneting.domain.Auth.dto.AuthUser;
import com.example.docconneting.domain.Auth.dto.request.UserRefreshTokenRequestDto;
import com.example.docconneting.domain.Auth.dto.request.UserSignUpRequestDto;
import com.example.docconneting.domain.Auth.dto.request.UserSigninRequestDto;
import com.example.docconneting.domain.Auth.dto.response.UserRefreshTokenResponseDto;
import com.example.docconneting.domain.Auth.dto.response.UserSignInResponseDto;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.enums.UserRole;
import com.example.docconneting.domain.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // 회원가입
    @Transactional
    public Response<Map<String, String>> signUp(UserSignUpRequestDto dto) {
        String password = passwordEncoder.encode(dto.getPassword());
        UserRole role = UserRole.of(dto.getUserRole().toUpperCase());

        User user;

        if (role == UserRole.DOCTOR) {
            if (dto.getMajor() == null) {
                throw new ClientException(ErrorCode.MAJOR_NOT_FOUND);
            }
            if (dto.getImage() == null) {
                throw new ClientException(ErrorCode.IMAGE_NOT_FOUND);
            }
            if (dto.getStartTime() == null) {
                throw new ClientException(ErrorCode.STARTTIME_NOT_FOUND);
            }
            if (dto.getEndTime() == null) {
                throw new ClientException(ErrorCode.ENDTIME_NOT_FOUND);
            }

            user = new User(
                    dto.getEmail(),
                    password,
                    dto.getUsername(),
                    Major.of(dto.getMajor().toUpperCase()),
                    dto.getImage(),
                    dto.getStartTime(),
                    dto.getEndTime(),
                    false,
                    role
            );

        } else if (role == UserRole.ADMIN) {
            user = new User(dto.getEmail(), password, dto.getUsername(), 0, false, role);

        } else {
            user = new User(dto.getEmail(), password, dto.getUsername(), 0, false, UserRole.PATIENT);
        }

        userRepository.save(user);

        Map<String, String> message = new HashMap<>();
        message.put("message", "회원 가입이 성공적으로 됐습니다");
        return Response.of(message);
    }

    // 로그인
    @Transactional(readOnly = true)
    public UserSignInResponseDto signIn(UserSigninRequestDto requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new ClientException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new ClientException(ErrorCode.INVALID_PASSWORD);
        }

        String accessToken = jwtUtil.createToken(user.getId(), user.getUserRole());
        String refreshToken = jwtUtil.createRefreshToken(user.getId());
        // refreshToken을 Redis에 저장
        refreshTokenService.saveRefreshToken(user.getId(), refreshToken);
        return UserSignInResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    //토큰 재발급
    @Transactional
    public UserRefreshTokenResponseDto refreshToken(AuthUser authuser, UserRefreshTokenRequestDto dto) {
        User user = userRepository.findById(authuser.getId())
                .orElseThrow(() -> new ClientException(ErrorCode.USER_NOT_FOUND));
        String token = jwtUtil.substringToken(dto.getRefreshToken());
        Claims claims = jwtUtil.extractClaims(token);

        String savedToken = refreshTokenService.getRefreshToken(user.getId());
        if (!token.equals(savedToken)) {
            throw new ClientException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String newAccessToken = jwtUtil.createToken(user.getId(), user.getUserRole());

        return UserRefreshTokenResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(savedToken)
                .build();
    }
}
