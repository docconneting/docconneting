package com.example.docconneting.domain.chatting.service;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.chatting.dto.response.ChattingRoomCreateResponse;
import com.example.docconneting.domain.chatting.entity.ChattingRoom;
import com.example.docconneting.domain.chatting.repository.ChattingRoomRepository;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.enums.UserRole;
import com.example.docconneting.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChattingRoomServiceTest {
    @Mock
    ChattingRoomRepository chattingRoomRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    ChattingRoomService chattingRoomService;

    @Test
    @DisplayName("채팅룸 생성시 권한이 환자가 아닐 때")
    void createChattingRoomUserRoleNotPatient(){
        // given
        Long userId = 1L;
        Long doctorId = 2L;
        UserRole userRole = UserRole.DOCTOR;
        AuthUser authUser = AuthUser.of(userId, userRole);

        // when, then
        ClientException clientException = assertThrows(ClientException.class, () -> chattingRoomService.createdChattingRoom(authUser, doctorId));
        assertThat(clientException.getErrorCode()).isEqualTo(ErrorCode.ONLY_PATIENT_CAN_CREATE_CHATTING_ROOM);
    }

    @Test
    @DisplayName("채팅룸 생성시 환자가 없을 때")
    void createChattingRoomPatientNotFound(){
        // given
        Long userId = 1L;
        Long doctorId = 2L;
        UserRole userRole = UserRole.PATIENT;
        AuthUser authUser = AuthUser.of(userId, userRole);

        given(userRepository.findByPatientId(userId)).willReturn(Optional.empty());

        // when, then
        ClientException clientException = assertThrows(ClientException.class, () -> chattingRoomService.createdChattingRoom(authUser, doctorId));
        assertThat(clientException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("채팅룸 생성시 의사가 없을 때")
    void createChattingRoomDoctorNotFound(){
        // given
        Long userId = 1L;
        Long doctorId = 2L;
        UserRole userRole = UserRole.PATIENT;
        AuthUser authUser = AuthUser.of(userId, userRole);

        User user = new User();

        given(userRepository.findByPatientId(userId)).willReturn(Optional.of(user));
        given(userRepository.findByDoctorId(doctorId)).willReturn(Optional.empty());

        // when, then
        ClientException clientException = assertThrows(ClientException.class, () -> chattingRoomService.createdChattingRoom(authUser, doctorId));
        assertThat(clientException.getErrorCode()).isEqualTo(ErrorCode.DOCTOR_NOT_FOUND);
    }

    @Test
    @DisplayName("채팅룸 생성시 채팅룸이 존재하고 이미 활성화 상태일 때")
    void createChattingRoomChattingRoomAlreadyExistAndActive(){
        // given
        Long userId = 1L;
        Long doctorId = 2L;
        UserRole userRole = UserRole.PATIENT;
        AuthUser authUser = AuthUser.of(userId, userRole);

        User patient = new User();
        ReflectionTestUtils.setField(patient, "id", userId);
        User doctor = new User();
        ReflectionTestUtils.setField(doctor, "id", doctorId);

        ChattingRoom chattingRoom = new ChattingRoom();
        ReflectionTestUtils.setField(chattingRoom, "isActive", true);

        given(userRepository.findByPatientId(userId)).willReturn(Optional.of(patient));
        given(userRepository.findByDoctorId(doctorId)).willReturn(Optional.of(doctor));

        given(chattingRoomRepository.findChattingRoomByPatientAndDoctor(userId, doctorId)).willReturn(Optional.of(chattingRoom));

        // when, then
        ClientException clientException = assertThrows(ClientException.class, () -> chattingRoomService.createdChattingRoom(authUser, doctorId));
        assertThat(clientException.getErrorCode()).isEqualTo(ErrorCode.CHATTING_ROOM_ALREADY_EXIST);
    }

    @Test
    @DisplayName("채팅룸 생성시 채팅룸이 존재하고 비활성화 상태일 때")
    void createChattingRoomChattingRoomAlreadyExistAndNotActive(){
        // given
        Long userId = 1L;
        Long doctorId = 2L;
        Long chattingRoomId = 1L;
        UserRole userRole = UserRole.PATIENT;
        AuthUser authUser = AuthUser.of(userId, userRole);
        LocalDateTime createdAt = LocalDateTime.now();

        User patient = new User();
        ReflectionTestUtils.setField(patient, "id", userId);
        User doctor = new User();
        ReflectionTestUtils.setField(doctor, "id", doctorId);

        ChattingRoom chattingRoom = new ChattingRoom();
        ReflectionTestUtils.setField(chattingRoom, "id", chattingRoomId);
        ReflectionTestUtils.setField(chattingRoom, "isActive", false);
        ReflectionTestUtils.setField(chattingRoom, "createdAt", createdAt);

        given(userRepository.findByPatientId(userId)).willReturn(Optional.of(patient));
        given(userRepository.findByDoctorId(doctorId)).willReturn(Optional.of(doctor));

        given(chattingRoomRepository.findChattingRoomByPatientAndDoctor(userId, doctorId)).willReturn(Optional.of(chattingRoom));

        // when
        ChattingRoomCreateResponse chattingRoomCreateResponse = chattingRoomService.createdChattingRoom(authUser, doctorId);

        // then
        assertThat(chattingRoomCreateResponse.getId()).isEqualTo(chattingRoomId);
        assertThat(chattingRoomCreateResponse.getPatientId()).isEqualTo(userId);
        assertThat(chattingRoomCreateResponse.getDoctorId()).isEqualTo(doctorId);
        assertThat(chattingRoomCreateResponse.getIsRecovered()).isEqualTo(true);
        assertThat(chattingRoomCreateResponse.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("채팅룸 생성시 채팅룸이 존재하지 않을 경우")
    void createChattingRoomChattingRoomNotExist(){
        // given
        Long userId = 1L;
        Long doctorId = 2L;
        Long chattingRoomId = 1L;
        UserRole userRole = UserRole.PATIENT;
        AuthUser authUser = AuthUser.of(userId, userRole);
        LocalDateTime createdAt = LocalDateTime.now();

        User patient = new User();
        ReflectionTestUtils.setField(patient, "id", userId);
        User doctor = new User();
        ReflectionTestUtils.setField(doctor, "id", doctorId);

        ChattingRoom chattingRoom = ChattingRoom.of(doctor, patient, true);
        ChattingRoom savedChattingRoom = new ChattingRoom();
        ReflectionTestUtils.setField(savedChattingRoom, "id", chattingRoomId);
        ReflectionTestUtils.setField(savedChattingRoom, "createdAt", createdAt);

        given(userRepository.findByPatientId(userId)).willReturn(Optional.of(patient));
        given(userRepository.findByDoctorId(doctorId)).willReturn(Optional.of(doctor));

        given(chattingRoomRepository.findChattingRoomByPatientAndDoctor(userId, doctorId)).willReturn(Optional.empty());

        given(chattingRoomRepository.save(refEq(chattingRoom))).willReturn(savedChattingRoom);

        // when
        ChattingRoomCreateResponse chattingRoomCreateResponse = chattingRoomService.createdChattingRoom(authUser, doctorId);

        // then
        assertThat(chattingRoomCreateResponse.getId()).isEqualTo(chattingRoomId);
        assertThat(chattingRoomCreateResponse.getPatientId()).isEqualTo(userId);
        assertThat(chattingRoomCreateResponse.getDoctorId()).isEqualTo(doctorId);
        assertThat(chattingRoomCreateResponse.getIsRecovered()).isEqualTo(false);
        assertThat(chattingRoomCreateResponse.getCreatedAt()).isEqualTo(createdAt);
    }


}