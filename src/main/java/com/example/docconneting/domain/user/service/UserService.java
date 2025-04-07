package com.example.docconneting.domain.user.service;

import com.example.docconneting.common.config.PasswordEncoder;
import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.user.dto.request.UpdateImageRequest;
import com.example.docconneting.domain.user.dto.request.UpdatePasswordRequest;
import com.example.docconneting.domain.user.dto.response.UserMyPageResponse;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.enums.UserRole;
import com.example.docconneting.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    //마이페이지 조회
    @Transactional(readOnly = true)
    public UserMyPageResponse findMyPage(AuthUser authUser) {
        User user = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new ClientException(ErrorCode.USER_NOT_FOUND));

        if (user.getUserRole().equals(UserRole.DOCTOR)) {
            return UserMyPageResponse.of(user.getUsername());
        }

        return UserMyPageResponse.of(user.getUsername(), user.getPoint());
    }

    //비밀번호 수정
    @Transactional
    public Map<String, String> updatePassword(AuthUser authUser, UpdatePasswordRequest dto) {
        User user = userRepository.findById(authUser.getId()).orElseThrow(()-> new ClientException(ErrorCode.USER_NOT_FOUND));
        if(!passwordEncoder.matches(dto.getOldPassword(),user.getPassword()))
        {
            throw new ClientException(ErrorCode.INVALID_PASSWORD);
        }
        if (dto.getOldPassword().equals(dto.getNewPassword())) {
            throw new ClientException(ErrorCode.PASSWORD_SAME_AS_OLD);
        }
        user.updatePassword(passwordEncoder.encode(dto.getNewPassword()));
        Map<String, String> message = new HashMap<>();
        message.put("message","비밀 번호 수정이 성공적으로 됐습니다");
        return message;
    }

    //의사 이미지 수정
    @Transactional
    public Map<String, String> updateImage(AuthUser authUser, UpdateImageRequest dto) {
        User user = userRepository.findById(authUser.getId()).orElseThrow(()-> new ClientException(ErrorCode.USER_NOT_FOUND));
        if(!user.getUserRole().equals(UserRole.DOCTOR))
        {
            throw new ClientException(ErrorCode.UNAUTHORIZED_USER);
        }
        user.updateImage(dto.getNewImage());
        Map<String, String> message = new HashMap<>();
        message.put("message","이미지 수정이 성공적으로 됐습니다");
        return message;
    }
}
