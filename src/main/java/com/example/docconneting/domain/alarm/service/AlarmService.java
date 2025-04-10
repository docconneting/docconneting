package com.example.docconneting.domain.alarm.service;

import com.example.docconneting.common.enums.Major;
import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.common.response.PageInfo;
import com.example.docconneting.common.response.PageResult;
import com.example.docconneting.domain.alarm.dto.AlarmResponse;
import com.example.docconneting.domain.alarm.entity.AlarmHistories;
import com.example.docconneting.domain.alarm.enums.AlarmType;
import com.example.docconneting.domain.alarm.event.AlarmEvent;
import com.example.docconneting.domain.alarm.repository.AlarmHistoriesBulkRepository;
import com.example.docconneting.domain.alarm.repository.AlarmHistoriesRepository;
import com.example.docconneting.domain.auth.dto.request.UserSignInRequest;
import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.repository.UserRepository;
import com.google.api.core.ApiFuture;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlarmService {

    private final UserRepository userRepository;
    private final AlarmHistoriesRepository alarmHistoriesRepository;
    private final AlarmHistoriesBulkRepository alarmHistoriesBulkRepository;
    private final ApplicationEventPublisher eventPublisher;

    /*
     * 로그인을 진행할 때 프론트에서 넘겨준 FCM 토큰과 알람 수락 권한을 데이터베이스에 저장
     */
    @Transactional
    public void saveFcmToken(UserSignInRequest requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new ClientException(ErrorCode.USER_NOT_FOUND));

        String fcmToken = requestDto.getFcmToken();
        boolean isTokenPresent = userRepository.existsByFcmToken(fcmToken);

        // fcm 토큰이 존재하지 않는다면 토큰과 알람 수락 권한 저장
        // TODO : 개발이 다 완료되면 마무리 할 때, 프론트에서 권한까지 같이 전송하는걸로 수정
        if (!isTokenPresent) {
            user.updateAlarmInfo(requestDto.getFcmToken(), true);
        }

        // 기존에 가지고 있던 fcm 토큰과 클라이언트로부터 받은 fcm 토큰이 다르다면 fcm 토큰을 업데이트
        if (isTokenPresent && !user.getFcmToken().equals(fcmToken)){
            user.updateFcmToken(requestDto.getFcmToken());
        }
    }

    /*
     * 사용자가 유료 게시물을 올렸을 떄 해당 전공에 해당되는 의사들에게 알람 전송
     */
    public void sendPostUploadCompletedMessage(Major major) {
        List<User> users = userRepository.findByMajor(major);
        List<AlarmHistories> alarmHistoriesList = new ArrayList<>();
        for (User user : users) {
            AlarmEvent event = AlarmEvent.of(
                    "새로운 유료 질문이 올라왔습니다!"
                    , user.getFcmToken()
                    , user.getId());

            eventPublisher.publishEvent(event);
            AlarmHistories alarmHistories = AlarmHistories.of(
                    "새로운 유료 질문이 올라왔습니다!"
                    , user.getId()
                    , AlarmType.POST_UPLOAD);
            alarmHistoriesList.add(alarmHistories);
        }
        alarmHistoriesBulkRepository.batchUpdate(alarmHistoriesList);
    }

    public void sendMessage(Message message, String token){

        ApiFuture<String> apiFuture = FirebaseMessaging.getInstance().sendAsync(message);

        Runnable task = () -> {
            try {
                String response = apiFuture.get();
                log.info("알림 전송 성공 : " + response);
                log.info("현재 스레드 NAME: " + Thread.currentThread().getName());
                log.info("📝 받은 사람 토큰 : " + token);

            } catch (InterruptedException | ExecutionException e) {
                log.error("FCM 알림 스레드에서 문제가 발생했습니다.", e);
                Thread.currentThread().interrupt();
            }
        };

        new Thread(task).start();
    }

    /*
     * 게시물에 의사가 댓글을 달았을 때 게시물 작성자에게 알람 전송
     */
    @Transactional
    public void sendCommentCompletedMessage(User user) {
        Message message = Message.builder()
                .setToken(user.getFcmToken())
                .setNotification(Notification.builder()
                        .setBody("회원님의 게시물에 의사가 답변을 달았습니다")
                        .build())
                .build();

        AlarmHistories alarmHistories = AlarmHistories.of(
                "회원님의 게시물에 의사가 답변을 달았습니다"
                , user.getId()
                , AlarmType.COMMENT);
        FirebaseMessaging.getInstance().sendAsync(message);
        alarmHistoriesRepository.save(alarmHistories);
    }

    /*
     * 채팅 진료 결제가 완료 됐을 때 해당 의사에게 알람 전송
     */
    @Transactional
    public void sendMedicalRequestMessage(User patient, User doctor) {
        String patientName = patient.getUsername();

        Message message = Message.builder()
                .setToken(doctor.getFcmToken())
                .setNotification(Notification.builder()
                        .setBody(patientName + "님이 채팅 진료를 요청 했습니다")
                        .build())
                .build();

        AlarmHistories alarmHistories = AlarmHistories.of(
                patientName + "님이 채팅 진료를 요청했습니다"
                , doctor.getId()
                , AlarmType.MEDICAL_REQUEST);
        FirebaseMessaging.getInstance().sendAsync(message);
        alarmHistoriesRepository.save(alarmHistories);
    }

    /*
     * 알람 목록 조회
     */
    @Transactional(readOnly = true)
    public PageResult<AlarmResponse> findAlarms(AuthUser authUser, Pageable pageable) {
        Page<AlarmHistories> result = alarmHistoriesRepository.findAlarmHistories(authUser.getId(), pageable);
        List<AlarmResponse> alarms = AlarmResponse.toAlarmResponse(result.getContent());

        PageInfo pageInfo = PageInfo.builder()
                .pageNum(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalElement(result.getTotalElements())
                .totalPage(result.getTotalPages())
                .build();

        return new PageResult<>(alarms, pageInfo);
    }
}
