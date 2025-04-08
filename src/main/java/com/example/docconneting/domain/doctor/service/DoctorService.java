package com.example.docconneting.domain.doctor.service;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.common.response.PageInfo;
import com.example.docconneting.common.response.PageResult;
import com.example.docconneting.domain.doctor.dto.DoctorResponse;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final UserRepository userRepository;

    // 의사 단건 조회
    public DoctorResponse findDoctor(Long id) {
        User user = userRepository.findByDoctorId(id).orElseThrow(() -> new ClientException(ErrorCode.DOCTOR_NOT_FOUND));

        DoctorResponse response = DoctorResponse.of(
                user.getId(),
                user.getUsername(),
                user.getMajor().name(),
                user.getImage(),
                user.getStartTime(),
                user.getEndTime());

        return response;
    }

    // 의사 다건 조회 검색
    public PageResult<DoctorResponse> findDoctors(Pageable pageable, String category, String name) {
        Page<User> result = userRepository.findDoctors(pageable, category, name);
        List<DoctorResponse> doctors = DoctorResponse.toDoctorResponse(result.getContent());

        PageInfo pageInfo = PageInfo.builder()
                .pageNum(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalElement(result.getTotalElements())
                .totalPage(result.getTotalPages())
                .build();

        return new PageResult<>(doctors, pageInfo);
    }
}
