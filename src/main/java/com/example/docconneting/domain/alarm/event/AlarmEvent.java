package com.example.docconneting.domain.alarm.event;

import lombok.Getter;

@Getter
public class AlarmEvent {

    private final String content;
    private final String fcmToken;
    private final Long userId;

    private AlarmEvent(String content, String fcmToken, Long userId) {
        this.content = content;
        this.fcmToken = fcmToken;
        this.userId = userId;
    }

    public static AlarmEvent of(String content, String fcmToken, Long userId) {
        return new AlarmEvent(content, fcmToken, userId);
    }
}
