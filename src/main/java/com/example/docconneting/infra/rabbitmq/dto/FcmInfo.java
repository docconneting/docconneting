package com.example.docconneting.infra.rabbitmq.dto;

import lombok.Getter;

@Getter
public class FcmInfo {
    private final String fcmToken;
    private final Long userId;

    private FcmInfo(String fcmToken, Long userId) {
        this.fcmToken = fcmToken;
        this.userId = userId;
    }

    public static FcmInfo of(String fcmToken, Long userId) {
        return new FcmInfo(fcmToken, userId);
    }
}
