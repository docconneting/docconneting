package com.example.docconneting.domain.alarm.service;

import com.example.docconneting.domain.alarm.entity.AlarmHistories;
import com.example.docconneting.domain.alarm.enums.AlarmType;
import com.example.docconneting.domain.alarm.repository.AlarmHistoriesRepository;
import com.google.api.core.ApiFuture;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncAlarmSender {

    private final AlarmHistoriesRepository alarmHistoriesRepository;

    @Async
    public void handleAlarm(Long userId, String fcmToken) {
        Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(Notification.builder()
                        .setBody("새로운 유료 질문이 올라왔습니다!")
                        .build())
                .build();

        AlarmHistories alarmHistories = AlarmHistories.of(
                "새로운 유료 질문이 올라왔습니다!"
                , userId
                , AlarmType.POST_UPLOAD);

        log.info("AlarmSender 호출");
        this.sendMessage(message, fcmToken, alarmHistories);
    }

    public void sendMessage(Message message, String token, AlarmHistories alarmHistories){
        ApiFuture<String> apiFuture = FirebaseMessaging.getInstance().sendAsync(message);
        alarmHistoriesRepository.save(alarmHistories);
        log.info("받은 사람 토큰 : " + token);
    }
}
