package com.example.docconneting.domain.alarm.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmSenderService {

    /*
     * 다건 알람 전송
     */
    @Async
    public void sendMulticastAlarm(List<String> fcmTokenBatche, String content) {
        MulticastMessage message = MulticastMessage.builder()
                .addAllTokens(fcmTokenBatche)
                .setNotification(Notification.builder()
                        .setBody(content)
                        .build())
                .build();

        FirebaseMessaging.getInstance().sendMulticastAsync(message);
    }

    /*
     * 단건 알람 전송
     */
    @Async
    public void sendAlarm(String fcmToken, String content) {
        Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(Notification.builder()
                        .setBody(content)
                        .build())
                .build();

        FirebaseMessaging.getInstance().sendAsync(message);
    }

}
