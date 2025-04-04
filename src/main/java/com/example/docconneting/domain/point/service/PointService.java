package com.example.docconneting.domain.point.service;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.domain.point.dto.response.PointResponse;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public PointResponse findPoint(Long userId) {

       User user = userRepository.findById(userId).orElseThrow(() ->
               new ClientException(ErrorCode.USER_NOT_FOUND));

       return new PointResponse(user.getPoint());
    }
}
