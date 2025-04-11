package com.example.docconneting.domain.alarm.controller;

import com.example.docconneting.common.response.PageResult;
import com.example.docconneting.common.response.Response;
import com.example.docconneting.domain.alarm.dto.AlarmResponse;
import com.example.docconneting.domain.alarm.service.AlarmService;
import com.example.docconneting.domain.auth.annotation.Auth;
import com.example.docconneting.domain.auth.entity.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;

    @GetMapping("")
    public ResponseEntity<Response<List<AlarmResponse>>> findAlarmHistories(@Auth AuthUser authUser, @PageableDefault Pageable pageable) {
        PageResult<AlarmResponse> pageResult = alarmService.findAlarms(authUser, pageable);
        return ResponseEntity.ok().body(Response.of(pageResult.getContent(), pageResult.getPageInfo()));
    }


    @PostMapping("/test")
    public void sendPostUploadAlarm() {
//        alarmService.sendPostUploadCompletedMessage(Major.SURGERY);
//        User user1 = userRepository.findById(4L).orElseThrow(() -> new RuntimeException("없네요"));
//        User user2 = userRepository.findById(2L).orElseThrow(() -> new RuntimeException("없네요"));
//        alarmService.sendCommentCompletedMessage(user1);
//        alarmService.sendMedicalRequestMessage(user1, user2);
    }

}
