package com.example.docconneting.domain.alarm.service;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ServerException;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.google.firebase.messaging.MessagingErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmSenderService {

    private final FcmTokenService fcmTokenService;

    /*
     * 다건 알람 전송
     */
    @Async("fcmExecutor")
    public void sendMulticastAlarm(List<String> fcmTokenBatche, String content) {
        try {
            MulticastMessage message = MulticastMessage.builder()
                    .setNotification(Notification.builder()
                            .setTitle("Docconneting")
                            .setBody(content)
                            .build())
                    .addAllTokens(fcmTokenBatche)
                    .build();

            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);

            int successCount = response.getSuccessCount();
            int failureCount = response.getFailureCount();

            log.info("알림 전송 완료 - 성공횟수: {}, 실패횟수: {}", successCount, failureCount);
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * 단건 알람 전송
     */
    @Async("fcmExecutor")
    @Retryable(
            retryFor = ServerException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void sendAlarm(String fcmToken, String content) {
        try {
            Message message = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(Notification.builder()
                            .setTitle("Docconneting")
                            .setBody(content)
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            log.info("알림 전송 완료 - 메시지 ID: {}", response);
        } catch (FirebaseMessagingException exception) {
            MessagingErrorCode errorCode = exception.getMessagingErrorCode();

            if (errorCode.equals(INTERNAL) || errorCode.equals(UNAVAILABLE)) {
                log.info("FCM 서버 내부 오류 발생 - 알람 전송 재시도");
                throw new ServerException(ErrorCode.FCM_SEND_FAILED);
            }

            if (errorCode.equals(INVALID_ARGUMENT) || errorCode.equals(UNREGISTERED)) {
                log.info("FCM 토큰 이상 발생 - 토큰 제거");
                fcmTokenService.deleteFcmToken(fcmToken);
            }

            if (errorCode.equals(THIRD_PARTY_AUTH_ERROR) || errorCode.equals(SENDER_ID_MISMATCH)) {
                log.info("서버 설정/인증서 문제 발생 - 서버 확인 필요");
            }
        }
    }

    @Recover
    public void recover(ServerException exception, String fcmToken, String content) {
        log.info("FCM 알림 재시도 3회 실패 - token : {}, content : {}", fcmToken, content);
    }

}
