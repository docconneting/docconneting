package com.example.docconneting.domain.alarm.event;

import com.example.docconneting.domain.alarm.service.AlarmService;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Async
@Component
@RequiredArgsConstructor
public class AlarmEventListener {

    private final AlarmService alarmService;
    @EventListener
    public void handleAlarmEvent(AlarmEvent alarmEvent) {
        log.info("event listener test");
        Message message = Message.builder()
                .setToken(alarmEvent.getFcmToken())
                .setNotification(Notification.builder()
                        .setBody(alarmEvent.getContent())
                        .build())
                .build();
        alarmService.sendMessage(message, alarmEvent.getFcmToken());
    }
}
