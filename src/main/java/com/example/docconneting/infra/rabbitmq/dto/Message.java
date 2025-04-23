package com.example.docconneting.infra.rabbitmq.dto;

import com.example.docconneting.domain.alarm.enums.AlarmType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class Message {
    // id값 추가, 이벤트가 처음 발생하는 시점에서 줘야 함
    private String fcmToken;
    private Long userId;
    private List<String> fcmTokenList;
    private List<Long> userIdList;
    private String message;
    private AlarmType alarmType;

    private Message(List<String> fcmTokenList, List<Long> userIdList, String message, AlarmType alarmType) {
        this.fcmTokenList = fcmTokenList;
        this.userIdList = userIdList;
        this.message = message;
        this.alarmType = alarmType;
    }

    private Message(String fcmToken, String message, Long userId, AlarmType alarmType) {
        this.fcmToken = fcmToken;
        this.message = message;
        this.userId = userId;
        this.alarmType = alarmType;
    }

    public static Message of(List<String> fcmTokenList, List<Long> userIdList, String message, AlarmType alarmType) {
        return new Message(fcmTokenList, userIdList, message, alarmType);
    }

    public static Message of(String fcmToken, String message, Long userId, AlarmType alarmType) {
        return new Message(fcmToken, message, userId, alarmType);
    }

}
