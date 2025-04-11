package com.example.docconneting.domain.alarm.service;

import com.example.docconneting.common.enums.Major;
import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.common.response.PageInfo;
import com.example.docconneting.common.response.PageResult;
import com.example.docconneting.domain.alarm.dto.AlarmResponse;
import com.example.docconneting.domain.alarm.entity.AlarmHistories;
import com.example.docconneting.domain.alarm.enums.AlarmType;
import com.example.docconneting.domain.alarm.repository.AlarmHistoriesBulkRepository;
import com.example.docconneting.domain.alarm.repository.AlarmHistoriesRepository;
import com.example.docconneting.domain.auth.dto.request.UserSignInRequest;
import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlarmService {

    private final UserRepository userRepository;
    private final AlarmSenderService alarmSenderService;
    private final AlarmHistoriesRepository alarmHistoriesRepository;
    private final AlarmHistoriesBulkRepository alarmHistoriesBulkRepository;

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
    @Transactional
    public void sendPostUploadCompletedMessage(Major major) {
        List<User> users = userRepository.findByMajor(major);
        List<String> fcmTokenList = users.stream().map(user -> user.getFcmToken()).toList();
        List<List<String>> fcmTokenBatches = new ArrayList<>();

        for (int i = 0; i < fcmTokenList.size(); i += 500) {
            fcmTokenBatches.add(fcmTokenList.subList(i, Math.min(fcmTokenList.size(), i + 500)));
        }

        for (List<String> fcmTokenBatche : fcmTokenBatches) {
            alarmSenderService.sendMulticastAlarm(fcmTokenBatche, "새로운 유료 질문이 올라왔습니다!");
        }
        alarmHistoriesBulkRepository.batchUpdate(users, AlarmType.POST_UPLOAD, "새로운 유료 질문이 올라왔습니다!");
    }

    /*
     * 게시물에 의사가 댓글을 달았을 때 게시물 작성자에게 알람 전송
     */
    @Transactional
    public void sendCommentCompletedMessage(User user) {
        alarmSenderService.sendAlarm(user.getFcmToken(), "회원님의 게시물에 의사가 답변을 달았습니다");
        saveAlarmHistories("회원님의 게시물에 의사가 답변을 달았습니다", user.getId(), AlarmType.COMMENT);
    }

    /*
     * 채팅 진료 결제가 완료 됐을 때 해당 의사에게 알람 전송
     */
    @Transactional
    public void sendMedicalRequestMessage(User patient, User doctor) {
        String patientName = patient.getUsername();
        alarmSenderService.sendAlarm(doctor.getFcmToken(), patientName + "님이 채팅 진료를 요청 했습니다");
        saveAlarmHistories(patientName + "님이 채팅 진료를 요청 했습니다", doctor.getId(), AlarmType.MEDICAL_REQUEST);
    }


    /*
     * 단건 알람 히스토리 저장
     */
    private void saveAlarmHistories(String content, Long id, AlarmType alarmType) {
        AlarmHistories alarmHistories = AlarmHistories.of(content, id, alarmType);
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
