package com.example.docconneting.domain.auth.service;

import com.example.docconneting.common.config.JwtUtil;
import com.example.docconneting.common.config.PasswordEncoder;
import com.example.docconneting.common.enums.Major;
import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.auth.dto.request.UserRefreshTokenRequest;
import com.example.docconneting.domain.auth.dto.request.UserSignUpRequest;
import com.example.docconneting.domain.auth.dto.request.UserSignInRequest;
import com.example.docconneting.domain.auth.dto.response.UserRefreshTokenResponse;
import com.example.docconneting.domain.auth.dto.response.UserSignInResponse;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.enums.UserRole;
import com.example.docconneting.domain.user.repository.UserRepository;
//import com.example.docconneting.domain.user.service.S3Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
//    private final S3Service s3Service;

    // 회원가입
    @Transactional
    public Map<String, String> signUp(UserSignUpRequest dto, MultipartFile multipartFile) throws IOException {
        if (userRepository.findByEmail(dto.getEmail()).isPresent())
        {
            throw new ClientException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }


        String password = passwordEncoder.encode(dto.getPassword());
        UserRole role = UserRole.of(dto.getUserRole().toUpperCase());


        User user = switch (role) {
            case DOCTOR -> {
                if (dto.getMajor() == null) {
                    throw new ClientException(ErrorCode.MAJOR_NOT_FOUND);
                }
                if (multipartFile == null) {
                    throw new ClientException(ErrorCode.IMAGE_NOT_FOUND);
                }
                if (dto.getStartTime() == null) {
                    throw new ClientException(ErrorCode.STARTTIME_NOT_FOUND);
                }
                if (dto.getEndTime() == null) {
                    throw new ClientException(ErrorCode.ENDTIME_NOT_FOUND);
                }
                Major major = Major.of(dto.getMajor().toUpperCase());
                yield User.of(
                        dto.getEmail(),
                        password,
                        dto.getUsername(),
                        major,
                        null,
                        dto.getStartTime(),
                        dto.getEndTime(),
                        false,
                        role
                );
            }

            case ADMIN -> User.of(
                    dto.getEmail(),
                    password,
                    dto.getUsername(),
                    0,
                    false,
                    role
            );

            case PATIENT -> User.of(
                    dto.getEmail(),
                    password,
                    dto.getUsername(),
                    0,
                    false,
                    UserRole.PATIENT
            );
        };

        //유저 저장 -> ID 생성
        userRepository.save(user);

//         //의사인 경우 이미지 url 삽입하기
//         if (role == UserRole.DOCTOR) {
//             String imageUrl = s3Service.uploadImage(user.getId(),multipartFile);
//             user.updateImage(imageUrl); // 엔티티에 imageUrl을 세팅
//         }

        Map<String, String> message = new HashMap<>();
        message.put("message", "회원 가입이 성공적으로 됐습니다");
        return message;
    }

    // 로그인
    @Transactional(readOnly = true)
    public UserSignInResponse signIn(UserSignInRequest requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new ClientException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new ClientException(ErrorCode.INVALID_PASSWORD);
        }

        String accessToken = jwtUtil.createToken(user.getId(), user.getUserRole());
        String refreshToken = jwtUtil.createRefreshToken(user.getId());

        // refreshToken을 Redis에 저장
        refreshTokenService.saveRefreshToken(user.getId(), refreshToken);

        return UserSignInResponse.of(accessToken, refreshToken);
    }

    /*
     * 로그인을 진행할 때 프론트에서 넘겨준 FCM 토큰과 알람 수락 권한을 데이터베이스에 저장
     */
    @Transactional
    public void saveFcmToken(UserSignInRequest requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new ClientException(ErrorCode.USER_NOT_FOUND));

        String fcmToken = requestDto.getFcmToken();
        Long isTokenPresent = userRepository.existsByFcmToken(user.getId());
        boolean exist = isTokenPresent > 0;

        // fcm 토큰이 존재하지 않는다면 토큰과 알람 수락 권한 저장, 프론트 구현이 안 됐으므로 알람 수신 여부는 일단 true로 지정
        if (!exist) {
            user.updateAlarmInfo(requestDto.getFcmToken(), true);
        }

        // 기존에 가지고 있던 fcm 토큰과 클라이언트로부터 받은 fcm 토큰이 다르다면 fcm 토큰을 업데이트
        if (exist && !user.getFcmToken().equals(fcmToken)){
            user.updateFcmToken(requestDto.getFcmToken());
        }
    }

    //토큰 재발급
    @Transactional
    public UserRefreshTokenResponse refreshAccessToken(AuthUser authuser, UserRefreshTokenRequest dto) {
        User user = userRepository.findById(authuser.getId())
                .orElseThrow(() -> new ClientException(ErrorCode.USER_NOT_FOUND));

        // Redis에서 저장된 토큰 조회
        String savedToken = refreshTokenService.getRefreshToken(authuser.getId());

        //리프레시 토큰 만료 확인
        Long ttl = refreshTokenService.getRefreshTokenTTL(authuser.getId());
        if (ttl == null || ttl <= 0) {
            throw new ClientException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        //dto와 DB에 있는 리프레시 토큰 비교
        if (savedToken == null ||!dto.getRefreshToken().equals(savedToken)) {
            throw new ClientException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        //어세스 토큰 재발급
        String newAccessToken = jwtUtil.createToken(user.getId(), user.getUserRole());

        //리프레시 토큰 재발급
        String newRefreshToken = jwtUtil.createRefreshToken(user.getId());

        //리프레시 토큰 레디스에 업데이트
        refreshTokenService.saveRefreshToken(user.getId(), newRefreshToken);

        return UserRefreshTokenResponse.of(newAccessToken, newRefreshToken);
    }
}
