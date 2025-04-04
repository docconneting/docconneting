package com.example.docconneting.domain.doctor.service;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.domain.doctor.dto.DoctorResponse;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final UserRepository userRepository;

    // 의사 단건 조회
    public DoctorResponse findDoctor(Long id) {
        User user = userRepository.findByDoctorId(id).orElseThrow(() -> new ClientException(ErrorCode.DOCTOR_NOT_FOUND));

        DoctorResponse response = DoctorResponse.builder()
                .id(user.getId())
                .name(user.getUsername())
                .major(user.getMajor().name())
                .imageUrl(user.getImage())
                .startTime(user.getStartTime())
                .endTime(user.getEndTime())
                .build();

        return response;
    }
}
