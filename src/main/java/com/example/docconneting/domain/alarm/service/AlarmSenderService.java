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
                .setNotification(Notification.builder()
                        .setTitle("Docconneting")
                        .setBody(content)
                        .build())
                .addAllTokens(fcmTokenBatche)
                .build();

        FirebaseMessaging.getInstance().sendEachForMulticastAsync(message);

//        BatchResponse response = future.get();
//
//        System.out.println("성공: " + response.getSuccessCount());
//        System.out.println("실패: " + response.getFailureCount());
//
//        for (SendResponse res : response.getResponses()) {
//            if (res.isSuccessful()) {
//                System.out.println("전송 성공: " + res.getMessageId());
//            } else {
//                System.out.println("전송 실패: " + res.getException().getMessage());
//            }
//        }
    }

    /*
     * 단건 알람 전송
     */
    @Async
    public void sendAlarm(String fcmToken, String content) {
        Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(Notification.builder()
                        .setTitle("Docconneting")
                        .setBody(content)
                        .build())
                .build();

        FirebaseMessaging.getInstance().sendAsync(message);
        log.info("전송된 토큰 : {}", fcmToken);
    }

}
