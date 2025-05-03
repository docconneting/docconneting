package com.example.docconneting.domain.coupon.controller;

import com.example.docconneting.common.config.JwtUtil;
import com.example.docconneting.common.filter.JwtFilter;
import com.example.docconneting.common.resolver.AuthUserArgumentResolver;
import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.coupon.dto.response.IssueCouponResponse;
import com.example.docconneting.domain.coupon.service.*;
import com.example.docconneting.domain.user.enums.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@WebMvcTest(PatientCouponController.class)
@TestPropertySource(properties = {
        "jwt.secret.key=rC1fZ3pYzPiNL2RzZHYtN3Q5MHJsT3pkZVNyT2lYa2M"
})
@Import({JwtUtil.class, JwtFilter.class, AuthUserArgumentResolver.class})
class PatientCouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PatientCouponService patientCouponService;

    @MockitoBean
    private DistributedCouponService distributedCouponService;

    @MockitoBean
    private OptimisticLockService optimisticLockService;

    @MockitoBean
    private PessimisticLockService pessimisticLockService;

    @MockitoBean(name = "elasticsearchMappingContext")
    private MappingContext<?, ?> elasticsearchMappingContext;

    @MockitoBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockitoBean
    private RabbitCouponIssueProducer rabbitCouponIssueProducer;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    LocalDateTime now = LocalDateTime.of(2025, 4, 20, 18, 5, 36);
    LocalDateTime expiredAt = now.plusDays(7);
    Integer availableCount = 5;

    @Test
    @DisplayName("유저가 쿠폰 발급에 성공하면 응답이 반환된다.")
    void issueCouponSuccessTest() throws Exception {

        // given
        Long userId = 1L;
        UserRole userRole = UserRole.PATIENT;

        AuthUser authUser = AuthUser.of(userId, userRole);
        Long couponId = 100L;

        String accessToken = jwtUtil.createToken(userId, userRole);

        IssueCouponResponse response = IssueCouponResponse.of(userId, couponId, availableCount, now, expiredAt);

        given(distributedCouponService.issueCoupon(any(), eq(couponId))).willReturn(response);

        // when, then
        mockMvc.perform(post("/api/v1/user-coupons/{couponId}", couponId)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(response.getUserId()))
                .andExpect(jsonPath("$.data.couponId").value(response.getCouponId()))
                .andExpect(jsonPath("$.data.availableCount").value(response.getAvailableCount()))
                .andExpect(jsonPath("$.data.startDate").value(response.getStartDate().toString()))
                .andExpect(jsonPath("$.data.endDate").value(response.getEndDate().toString()));
    }
}