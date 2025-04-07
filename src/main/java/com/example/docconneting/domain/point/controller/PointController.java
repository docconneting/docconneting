package com.example.docconneting.domain.point.controller;

import com.example.docconneting.common.response.Response;
import com.example.docconneting.domain.point.dto.response.PointResponse;
import com.example.docconneting.domain.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    @GetMapping("/api/v1/points")
    public ResponseEntity<Response<PointResponse>> findPoint(@Auth AuthUser authUser) {
        Long userId = authUser.getId();
        PointResponse response = pointService.findPoint(userId);
        return ResponseEntity.ok(Response.of(response));
    }
}
