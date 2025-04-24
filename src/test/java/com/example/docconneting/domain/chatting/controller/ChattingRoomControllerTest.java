package com.example.docconneting.domain.chatting.controller;

import com.example.docconneting.common.config.JwtUtil;
import com.example.docconneting.common.filter.JwtFilter;
import com.example.docconneting.common.resolver.AuthUserArgumentResolver;
import com.example.docconneting.common.response.PageInfo;
import com.example.docconneting.common.response.PageResult;
import com.example.docconneting.domain.auth.annotation.Auth;
import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.chatting.dto.response.ChattingRoomCreateResponse;
import com.example.docconneting.domain.chatting.dto.response.ChattingRoomListResponse;
import com.example.docconneting.domain.chatting.dto.response.ChattingRoomSingleResponse;
import com.example.docconneting.domain.chatting.entity.ChattingRoom;
import com.example.docconneting.domain.chatting.service.ChattingRoomService;
import com.example.docconneting.domain.post.dto.reponse.PostListResponse;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.enums.UserRole;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChattingRoomController.class)
@TestPropertySource(properties = {
        "jwt.secret.key=5Gk6hibHDtKLFVk4NdBX039rvehSLNjfKsdXpm/pHsU="
})
@Import({JwtUtil.class, AuthUserArgumentResolver.class, JwtFilter.class})
class ChattingRoomControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ChattingRoomService chattingRoomService;

    @MockitoBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Autowired
    JwtUtil jwtUtil;

//    @Test
//    @DisplayName("채팅방 생성 api 테스트")
//    void createdChattingRoomTest() throws Exception {
//        // given
//        Long userId = 1L;
//        Long doctorId = 2L;
//        UserRole userRole = UserRole.PATIENT;
//        AuthUser authUser = AuthUser.of(userId, userRole);
//        Long chattingRoomId = 1L;
//        boolean isRecovered = false;
//        LocalDateTime createdAt = LocalDateTime.now();
//
//        String accessToken = jwtUtil.createToken(userId, userRole);
//
//        ChattingRoomCreateResponse chattingRoomCreateResponse = ChattingRoomCreateResponse.of(chattingRoomId, userId, doctorId, isRecovered, createdAt);
//
//        given(chattingRoomService.createdChattingRoom(refEq(authUser), eq(doctorId))).willReturn(chattingRoomCreateResponse);
//
//        // when, then
//        mockMvc.perform(post("/api/v1/doctors/{doctorId}/chattingRooms", doctorId)
//                        .header("Authorization", accessToken))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.id").value(chattingRoomId))
//                .andExpect(jsonPath("$.data.patientId").value(userId))
//                .andExpect(jsonPath("$.data.doctorId").value(doctorId))
//                .andExpect(jsonPath("$.data.isRecovered").value(isRecovered))
//                .andExpect(jsonPath("$.data.createdAt", Matchers.startsWith(createdAt.toString().substring(0,19))));
//
//        verify(chattingRoomService, times(1)).createdChattingRoom(refEq(authUser), eq(doctorId));
//    }
//
//    @Test
//    @DisplayName("채팅방 단건 조회 api 테스트")
//    void findChattingRoomByIdTest() throws Exception {
//        // given
//        Long userId = 1L;
//        Long doctorId = 2L;
//        UserRole userRole = UserRole.PATIENT;
//        AuthUser authUser = AuthUser.of(userId, userRole);
//        Long chattingRoomId = 1L;
//        LocalDateTime createdAt = LocalDateTime.now();
//
//        String accessToken = jwtUtil.createToken(userId, userRole);
//
//        ChattingRoomSingleResponse chattingRoomSingleResponsee = ChattingRoomSingleResponse.of(chattingRoomId, userId, doctorId, createdAt);
//
//        given(chattingRoomService.findChattingRoomById(refEq(authUser), eq(chattingRoomId))).willReturn(chattingRoomSingleResponsee);
//
//        // when, then
//        mockMvc.perform(get("/api/v1/chattingRooms/{chattingRoomId}", chattingRoomId)
//                        .header("Authorization", accessToken))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.id").value(chattingRoomId))
//                .andExpect(jsonPath("$.data.patientId").value(userId))
//                .andExpect(jsonPath("$.data.doctorId").value(doctorId))
//                .andExpect(jsonPath("$.data.createdAt", Matchers.startsWith(createdAt.toString().substring(0,19))));
//
//        verify(chattingRoomService, times(1)).findChattingRoomById(refEq(authUser), eq(chattingRoomId));
//    }

    @Test
    @DisplayName("채팅방 리스트 조회 api 테스트")
    void findAllChattingRoomsTest() throws Exception{
        // given
        Long userId = 1L;
        Long doctorId = 2L;
        UserRole userRole = UserRole.PATIENT;
        AuthUser authUser = AuthUser.of(userId, userRole);
        LocalDateTime createdAt = LocalDateTime.now();

        User doctor = User.of(null, null, null, null, null, null);
        ReflectionTestUtils.setField(doctor, "id", doctorId);

        Pageable pageable = PageRequest.of(0, 10);

        List<ChattingRoom> chattingRooms = new ArrayList<>();
        for(int i=0;i<10;i++){
            ChattingRoom chattingRoom = ChattingRoom.of(null, null, null);
            ReflectionTestUtils.setField(chattingRoom, "doctor", doctor);
            chattingRooms.add(chattingRoom);
        }

        List<ChattingRoomListResponse> content = ChattingRoomListResponse.toChattingRoomListResponsesForPatient(chattingRooms);

        PageInfo pageInfo = PageInfo.builder()
                .pageNum(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalElement(content.size())
                .totalPage(content.size()/pageable.getPageSize())
                .build();

        PageResult<ChattingRoomListResponse> pageResult = new PageResult<>(content, pageInfo);

        String accessToken = jwtUtil.createToken(userId, userRole);

        given(chattingRoomService.findAllChattingRooms(refEq(authUser), argThat(
                p -> p.getPageNumber() == pageable.getPageNumber() && p.getPageSize() == pageable.getPageSize()
        ))).willReturn(pageResult);

        // when, then
        mockMvc.perform(get("/api/v1/chattingRooms")
                .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(content.size()))
                .andExpect(jsonPath("$.page.pageNum").value(pageInfo.getPageNum()))
                .andExpect(jsonPath("$.page.pageSize").value(pageInfo.getPageSize()))
                .andExpect(jsonPath("$.page.totalElement").value(pageInfo.getTotalElement()))
                .andExpect(jsonPath("$.page.totalPage").value(pageInfo.getTotalPage()));

        verify(chattingRoomService, times(1)).findAllChattingRooms(refEq(authUser), argThat(
                p -> p.getPageNumber() == pageable.getPageNumber() && p.getPageSize() == pageable.getPageSize()
        ));
    }

    @Test
    @DisplayName("채팅방 비활성화 api 테스트")
    void deletedChattingRoomByIdTest() throws Exception {
        // given
        Long userId = 1L;
        UserRole userRole = UserRole.PATIENT;
        AuthUser authUser = AuthUser.of(userId, userRole);
        Long chattingRoomId = 1L;

        String accessToken = jwtUtil.createToken(userId, userRole);

        doNothing().when(chattingRoomService).deleteChattingRoomById(refEq(authUser), eq(chattingRoomId));

        // when, then
        mockMvc.perform(delete("/api/v1/chattingRooms/{chattingRoomId}", chattingRoomId)
                        .header("Authorization", accessToken))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.data.message").value("채팅방이 성공적으로 비활성화 되었습니다."));

        verify(chattingRoomService).deleteChattingRoomById(refEq(authUser), eq(chattingRoomId));
    }
}