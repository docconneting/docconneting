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

import java.util.ArrayList;
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
        List<String> targets = new ArrayList<>(fcmTokenBatche);
        int maxAttempts = 3;
        int attempt = 1;

        while (attempt <= maxAttempts && !targets.isEmpty()) {
            MulticastMessage message = MulticastMessage.builder()
                    .setNotification(Notification.builder()
                            .setTitle("Docconneting")
                            .setBody(content)
                            .build())
                    .addAllTokens(targets)
                    .build();

            try {
                BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);

                List<String> failedTokens = new ArrayList<>();
                List<SendResponse> responses = response.getResponses();

                for (int i = 0; i < responses.size(); i++) {
                    SendResponse sendResponse = responses.get(i);
                    String fcmToken = targets.get(i);

                    if (!sendResponse.isSuccessful()) {
                        FirebaseMessagingException exception = (FirebaseMessagingException) sendResponse.getException();
                        MessagingErrorCode errorCode = exception.getMessagingErrorCode();

                        if (errorCode.equals(INTERNAL) || errorCode.equals(UNAVAILABLE)) {
                            log.error("FCM 서버 내부 오류 발생 - 알람 전송 재시도 리스트에 추가");
                            failedTokens.add(fcmToken);
                        }

                        if (errorCode.equals(INVALID_ARGUMENT) || errorCode.equals(UNREGISTERED)) {
                            log.error("FCM 토큰 이상 발생 - 토큰 제거");
                            fcmTokenService.deleteFcmToken(fcmToken);
                        }

                        if (errorCode.equals(THIRD_PARTY_AUTH_ERROR) || errorCode.equals(SENDER_ID_MISMATCH)) {
                            log.error("서버 설정/인증서 문제 발생 - 서버 확인 필요");
                        }
                    }
                }

                if (failedTokens.isEmpty()) {
                    log.info("알림 전송 완료 - 성공횟수 : {}, 실패횟수 : {}", response.getSuccessCount(), response.getFailureCount());
                    return;
                }

                targets = failedTokens;
                attempt++;

            } catch (FirebaseMessagingException e) {
                log.error("알람 전체 전송 실패 - {}", e.getMessagingErrorCode());
                return;
            }

        }

        if (!targets.isEmpty()) {
            log.error("알람 전송 최종 실패 명수 - {}", targets.size());
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
                log.error("FCM 서버 내부 오류 발생 - 알람 전송 재시도");
                throw new ServerException(ErrorCode.FCM_SEND_FAILED);
            }

            if (errorCode.equals(INVALID_ARGUMENT) || errorCode.equals(UNREGISTERED)) {
                log.error("FCM 토큰 이상 발생 - 토큰 제거");
                fcmTokenService.deleteFcmToken(fcmToken);
            }

            if (errorCode.equals(THIRD_PARTY_AUTH_ERROR) || errorCode.equals(SENDER_ID_MISMATCH)) {
                log.error("서버 설정/인증서 문제 발생 - 서버 확인 필요");
            }
        }
    }

    @Recover
    public void recover(ServerException exception, String fcmToken, String content) {
        log.info("FCM 알림 재시도 3회 실패 - token : {}, content : {}", fcmToken, content);
    }

}
