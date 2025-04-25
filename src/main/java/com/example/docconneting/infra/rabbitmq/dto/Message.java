package com.example.docconneting.infra.rabbitmq.dto;

import com.example.docconneting.domain.alarm.enums.AlarmType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class Message {
    private List<FcmInfo> fcmInfos;
    private String message;
    private AlarmType alarmType;

    private Message(List<FcmInfo> fcmInfos, String message, AlarmType alarmType) {
        this.fcmInfos = fcmInfos;
        this.message = message;
        this.alarmType = alarmType;
    }

    public static Message of(List<FcmInfo> fcmInfos, String message, AlarmType alarmType) {
        return new Message(fcmInfos, message, alarmType);
    }

}
