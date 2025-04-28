package com.example.docconneting.domain.coupon.controller;

import com.example.docconneting.common.config.JwtUtil;
import com.example.docconneting.common.filter.JwtFilter;
import com.example.docconneting.common.resolver.AuthUserArgumentResolver;
import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.coupon.dto.request.CreateCouponRequest;
import com.example.docconneting.domain.coupon.dto.response.CreateCouponResponse;
import com.example.docconneting.domain.coupon.entity.Coupon;
import com.example.docconneting.domain.coupon.service.CouponService;
import com.example.docconneting.domain.user.enums.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;


import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminCouponController.class)
@TestPropertySource(properties = {
        "jwt.secret.key=rC1fZ3pYzPiNL2RzZHYtN3Q5MHJsT3pkZVNyT2lYa2M"
})
@Import({JwtUtil.class, JwtFilter.class, AuthUserArgumentResolver.class})
class AdminCouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CouponService couponService;

    @MockitoBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    LocalDateTime now = LocalDateTime.of(2025, 4, 20, 18, 5, 36);
    LocalDateTime expiredAt = now.plusDays(7);

    @Test
    @DisplayName("운영자가 쿠폰 생성에 성공하면 쿠폰이 저장되고 응답이 반환된다.")
    void createCouponSuccessTest() throws Exception {

        // given
        Long userId = 1L;
        UserRole userRole = UserRole.ADMIN;
        AuthUser authUser = AuthUser.of(userId, userRole);

        String accessToken = jwtUtil.createToken(userId, userRole);

        CreateCouponRequest request = CreateCouponRequest.of(10);

        Coupon coupon = Coupon.of(5, 10, now, expiredAt);
        ReflectionTestUtils.setField(coupon, "id", 1L);

        CreateCouponResponse savedResponse = CreateCouponResponse.of(
                coupon.getId(),
                coupon.getAvailableCount(),
                coupon.getQuantity(),
                coupon.getStartDate(),
                coupon.getEndDate()
        );

        given(couponService.createCoupon(any(), any(), any())).willReturn(savedResponse);

        // when, then
        mockMvc.perform(post("/api/v1/coupons")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(savedResponse.getId()))
                .andExpect(jsonPath("$.data.availableCount").value(savedResponse.getAvailableCount()))
                .andExpect(jsonPath("$.data.quantity").value(savedResponse.getQuantity()))
                .andExpect(jsonPath("$.data.startDate").value(savedResponse.getStartDate().toString()))
                .andExpect(jsonPath("$.data.endDate").value(savedResponse.getEndDate().toString()));
    }
}