package com.example.docconneting.domain.Auth.service;

import com.example.docconneting.common.config.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;
    private final long REFRESH_TOKEN_EXPIRE_TIME = 14 * 24 * 60 * 60;

    //refreshToken 저장
    @Transactional
    public void saveRefreshToken(Long userId, String refreshToken) {
        redisTemplate.opsForValue().set("refreshToken:" + userId, refreshToken, Duration.ofSeconds(REFRESH_TOKEN_EXPIRE_TIME));
    }

    //refreshToken 찾기
    @Transactional(readOnly = true)
    public String getRefreshToken(Long userId) {
        return redisTemplate.opsForValue().get("refreshToken:" + userId);
    }

    //refreshToken 만료시간 확인
    @Transactional(readOnly = true)
    public Long getRefreshTokenTTL(Long userId) {
        return redisTemplate.getExpire("refreshToken:" + userId);
    }

}

