package com.example.docconneting.domain.user.service;

import com.example.docconneting.common.config.PasswordEncoder;
import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.domain.Auth.dto.AuthUser;
import com.example.docconneting.domain.user.dto.request.UpdatePasswordRequestDto;
import com.example.docconneting.domain.user.dto.response.UserMyPageResponseDto;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    //마이페이지 조회
    public UserMyPageResponseDto findMyPage(AuthUser authUser) {
        User user = userRepository.findById(authUser.getId()).orElseThrow(()-> new ClientException(ErrorCode.USER_NOT_FOUND));

        return UserMyPageResponseDto.builder()
                .username(user.getUsername())
                .point(user.getPoint())
                .build();
    }

    //비밀번호 수정
    public Map<String, String> updatePassword(AuthUser authUser, UpdatePasswordRequestDto dto) {
        User user = userRepository.findById(authUser.getId()).orElseThrow(()-> new ClientException(ErrorCode.USER_NOT_FOUND));
        if(!passwordEncoder.matches(dto.getOldPassword(),user.getPassword()))
        {
            throw new ClientException(ErrorCode.INVALID_PASSWORD);
        }
        
        return null;
    }
}
