package com.example.docconneting.domain.point.controller;

import com.example.docconneting.common.config.JwtUtil;
import com.example.docconneting.domain.point.dto.response.PointResponse;
import com.example.docconneting.domain.point.service.PointService;
import com.example.docconneting.domain.user.enums.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class PointControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtUtil jwtUtil;

    @MockitoBean
    private PointService pointService;

    @Test
    @DisplayName("포인트 조회 테스트")
    void findPointTest() throws Exception {
        // given
        long userId = 1L;
        int point = 1000;

        String accessToken = jwtUtil.createToken(userId, UserRole.PATIENT);

        PointResponse response = PointResponse.of(point);
        given(pointService.findPoint(userId)).willReturn(response);

        // when, then
        mockMvc.perform(get("/api/v1/points")
                .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.point").value(response.getPoint()));
    }
}