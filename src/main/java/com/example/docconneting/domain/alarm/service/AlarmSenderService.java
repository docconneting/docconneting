package com.example.docconneting.domain.alarm.service;

import com.example.docconneting.common.enums.Major;
import com.example.docconneting.domain.alarm.enums.AlarmType;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.repository.UserRepository;
import com.example.docconneting.infra.rabbitmq.dto.FcmInfo;
import com.example.docconneting.infra.rabbitmq.dto.Message;
import com.example.docconneting.infra.rabbitmq.producer.AlarmServerSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlarmSenderService {

    private final UserRepository userRepository;
    private final AlarmServerSender alarmServerSender;

    /*
     * 사용자가 유료 게시물을 올렸을 떄 해당 전공에 해당되는 의사들에게 알람 전송
     */
    @Transactional
    public void sendPostUploadCompletedMessage(Major major) {
        List<User> users = userRepository.findByMajor(major);
        List<FcmInfo> fcmInfoList = users.stream()
                                    .map(user -> FcmInfo.of(user.getFcmToken(), user.getId()))
                                    .toList();

        Message messageDto = Message.of(fcmInfoList, "새로운 유료 질문이 등록되었습니다", AlarmType.POST_UPLOAD);
        alarmServerSender.send(messageDto);
    }

    /*
     * 게시물에 의사가 댓글을 달았을 때 게시물 작성자에게 알람 전송
     */
    @Transactional
    public void sendCommentCompletedMessage(User user) {
        FcmInfo fcmInfo = FcmInfo.of(user.getFcmToken(), user.getId());
        List<FcmInfo> fcmInfoList = List.of(fcmInfo);
        String message = "회원님의 게시물에 의사가 답변을 달았습니다";
        Message messageDto = Message.of(fcmInfoList, message, AlarmType.COMMENT);
        alarmServerSender.send(messageDto);
    }

    /*
     * 채팅 진료 결제가 완료 됐을 때 해당 의사에게 알람 전송
     */
    @Transactional
    public void sendMedicalRequestMessage(User patient, User doctor) {
        FcmInfo fcmInfo = FcmInfo.of(doctor.getFcmToken(), doctor.getId());
        List<FcmInfo> fcmInfoList = List.of(fcmInfo);
        String message = patient.getUsername() + "님이 채팅 진료를 요청 했습니다";
        Message messageDto = Message.of(fcmInfoList, message, AlarmType.MEDICAL_REQUEST);
        alarmServerSender.send(messageDto);
    }

}
