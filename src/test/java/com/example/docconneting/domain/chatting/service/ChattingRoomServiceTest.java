package com.example.docconneting.domain.chatting.service;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.common.response.PageInfo;
import com.example.docconneting.common.response.PageResult;
import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.chatting.dto.response.ChattingRoomCreateResponse;
import com.example.docconneting.domain.chatting.dto.response.ChattingRoomListResponse;
import com.example.docconneting.domain.chatting.dto.response.ChattingRoomSingleResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

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

        verify(userRepository, times(0)).findByPatientId(userId);
        verify(userRepository, times(0)).findByDoctorId(doctorId);
        verify(chattingRoomRepository, times(0)).findChattingRoomByPatientAndDoctor(userId, doctorId);
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

        verify(userRepository, times(1)).findByPatientId(userId);
        verify(userRepository, times(0)).findByDoctorId(doctorId);
        verify(chattingRoomRepository, times(0)).findChattingRoomByPatientAndDoctor(userId, doctorId);
    }

    @Test
    @DisplayName("채팅룸 생성시 의사가 없을 때")
    void createChattingRoomDoctorNotFound(){
        // given
        Long userId = 1L;
        Long doctorId = 2L;
        UserRole userRole = UserRole.PATIENT;
        AuthUser authUser = AuthUser.of(userId, userRole);

        User user = User.of(null, null, null, null, null, null);

        given(userRepository.findByPatientId(userId)).willReturn(Optional.of(user));
        given(userRepository.findByDoctorId(doctorId)).willReturn(Optional.empty());

        // when, then
        ClientException clientException = assertThrows(ClientException.class, () -> chattingRoomService.createdChattingRoom(authUser, doctorId));
        assertThat(clientException.getErrorCode()).isEqualTo(ErrorCode.DOCTOR_NOT_FOUND);

        verify(userRepository, times(1)).findByPatientId(userId);
        verify(userRepository, times(1)).findByDoctorId(doctorId);
        verify(chattingRoomRepository, times(0)).findChattingRoomByPatientAndDoctor(userId, doctorId);
    }

    @Test
    @DisplayName("채팅룸 생성시 채팅룸이 존재하고 이미 활성화 상태일 때")
    void createChattingRoomChattingRoomAlreadyExistAndActive(){
        // given
        Long userId = 1L;
        Long doctorId = 2L;
        UserRole userRole = UserRole.PATIENT;
        AuthUser authUser = AuthUser.of(userId, userRole);

        User patient = User.of(null, null, null, null, null, null);
        ReflectionTestUtils.setField(patient, "id", userId);
        User doctor = User.of(null, null, null, null, null, null);
        ReflectionTestUtils.setField(doctor, "id", doctorId);

        ChattingRoom chattingRoom = ChattingRoom.of(null, null, null);
        ReflectionTestUtils.setField(chattingRoom, "isActive", true);

        given(userRepository.findByPatientId(userId)).willReturn(Optional.of(patient));
        given(userRepository.findByDoctorId(doctorId)).willReturn(Optional.of(doctor));

        given(chattingRoomRepository.findChattingRoomByPatientAndDoctor(userId, doctorId)).willReturn(Optional.of(chattingRoom));

        // when, then
        ClientException clientException = assertThrows(ClientException.class, () -> chattingRoomService.createdChattingRoom(authUser, doctorId));
        assertThat(clientException.getErrorCode()).isEqualTo(ErrorCode.CHATTING_ROOM_ALREADY_EXIST);

        verify(userRepository, times(1)).findByPatientId(userId);
        verify(userRepository, times(1)).findByDoctorId(doctorId);
        verify(chattingRoomRepository, times(1)).findChattingRoomByPatientAndDoctor(userId, doctorId);
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

        User patient = User.of(null, null, null, null, null, null);
        ReflectionTestUtils.setField(patient, "id", userId);
        User doctor = User.of(null, null, null, null, null, null);
        ReflectionTestUtils.setField(doctor, "id", doctorId);

        ChattingRoom chattingRoom = ChattingRoom.of(null, null, null);
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

        verify(userRepository, times(1)).findByPatientId(userId);
        verify(userRepository, times(1)).findByDoctorId(doctorId);
        verify(chattingRoomRepository, times(1)).findChattingRoomByPatientAndDoctor(userId, doctorId);
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

        User patient = User.of(null, null, null, null, null, null);
        ReflectionTestUtils.setField(patient, "id", userId);
        User doctor = User.of(null, null, null, null, null, null);
        ReflectionTestUtils.setField(doctor, "id", doctorId);

        ChattingRoom chattingRoom = ChattingRoom.of(doctor, patient, true);
        ChattingRoom savedChattingRoom = ChattingRoom.of(null, null, null);
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

        verify(userRepository, times(1)).findByPatientId(userId);
        verify(userRepository, times(1)).findByDoctorId(doctorId);
        verify(chattingRoomRepository, times(1)).findChattingRoomByPatientAndDoctor(userId, doctorId);
    }

    @Test
    @DisplayName("채팅방 조회시 채팅방이 존재하지 않음")
    void findChattingRoomByIdChattingRoomNotExist(){
        // given
        Long userId = 1L;
        Long chattingRoomId = 1L;
        UserRole userRole = UserRole.PATIENT;
        AuthUser authUser = AuthUser.of(userId, userRole);

        // when
        given(chattingRoomRepository.findChattingRoomWithPatientAndDoctor(chattingRoomId)).willReturn(Optional.empty());

        // then
        ClientException clientException = assertThrows(ClientException.class, () -> chattingRoomService.findChattingRoomById(authUser, chattingRoomId));

        assertThat(clientException.getErrorCode()).isEqualTo(ErrorCode.CHATTING_ROOM_NOT_FOUND);

        verify(chattingRoomRepository, times(1)).findChattingRoomWithPatientAndDoctor(chattingRoomId);
        verify(userRepository, times(0)).findByPatientId(userId);
        verify(userRepository, times(0)).findByDoctorId(userId);
    }

    @Test
    @DisplayName("채팅방 조회시 권한이 PATIENT 이고 존재하지 않는 유저일 때")
    void findChattingRoomByIdPatientNotExist(){
        // given
        Long userId = 1L;
        Long chattingRoomId = 1L;
        UserRole userRole = UserRole.PATIENT;
        AuthUser authUser = AuthUser.of(userId, userRole);

        ChattingRoom findChattingRoom = ChattingRoom.of(null, null, null);

        given(chattingRoomRepository.findChattingRoomWithPatientAndDoctor(chattingRoomId)).willReturn(Optional.of(findChattingRoom));

        given(userRepository.findByPatientId(userId)).willReturn(Optional.empty());

        // when
        ClientException clientException = assertThrows(ClientException.class, () -> chattingRoomService.findChattingRoomById(authUser, chattingRoomId));

        // then
        assertThat(clientException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);

        verify(chattingRoomRepository, times(1)).findChattingRoomWithPatientAndDoctor(chattingRoomId);
        verify(userRepository, times(1)).findByPatientId(userId);
        verify(userRepository, times(0)).findByDoctorId(userId);
    }

    @Test
    @DisplayName("채팅방 조회시 권한이 PATIENT 이고 채팅방에 권한이 없을 때")
    void findChattingRoomByIdInvalidPatient(){
        // given
        Long userId = 1L;
        Long findUserId = 2L;
        Long chattingRoomId = 1L;
        UserRole userRole = UserRole.PATIENT;
        AuthUser authUser = AuthUser.of(userId, userRole);

        User findUser = User.of(null, null, null, null, null, null);

        User patient = User.of(null, null, null, null, null, null);
        ReflectionTestUtils.setField(patient, "id", findUserId);

        ChattingRoom findChattingRoom = ChattingRoom.of(null, null, null);
        ReflectionTestUtils.setField(findChattingRoom, "patient", patient);

        given(chattingRoomRepository.findChattingRoomWithPatientAndDoctor(chattingRoomId)).willReturn(Optional.of(findChattingRoom));

        given(userRepository.findByPatientId(userId)).willReturn(Optional.of(findUser));

        // when
        ClientException clientException = assertThrows(ClientException.class, () -> chattingRoomService.findChattingRoomById(authUser, chattingRoomId));

        // then
        assertThat(clientException.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN_CHATTING_ROOM_ACCESS);

        verify(chattingRoomRepository, times(1)).findChattingRoomWithPatientAndDoctor(chattingRoomId);
        verify(userRepository, times(1)).findByPatientId(userId);
        verify(userRepository, times(0)).findByDoctorId(userId);
    }

    @Test
    @DisplayName("채팅방 조회시 권한이 DOCTOR 이고 존재하지 않는 유저일 때")
    void findChattingRoomByIdDoctorNotExist(){
        // given
        Long userId = 1L;
        Long chattingRoomId = 1L;
        UserRole userRole = UserRole.DOCTOR;
        AuthUser authUser = AuthUser.of(userId, userRole);

        ChattingRoom findChattingRoom = ChattingRoom.of(null, null, null);

        given(chattingRoomRepository.findChattingRoomWithPatientAndDoctor(chattingRoomId)).willReturn(Optional.of(findChattingRoom));

        given(userRepository.findByDoctorId(userId)).willReturn(Optional.empty());

        // when
        ClientException clientException = assertThrows(ClientException.class, () -> chattingRoomService.findChattingRoomById(authUser, chattingRoomId));

        // then
        assertThat(clientException.getErrorCode()).isEqualTo(ErrorCode.DOCTOR_NOT_FOUND);

        verify(chattingRoomRepository, times(1)).findChattingRoomWithPatientAndDoctor(chattingRoomId);
        verify(userRepository, times(0)).findByPatientId(userId);
        verify(userRepository, times(1)).findByDoctorId(userId);
    }

    @Test
    @DisplayName("채팅방 조회시 권한이 DOCTOR 이고 채팅방에 권한이 없을 때")
    void findChattingRoomByIdInvalidDoctor(){
        // given
        Long userId = 1L;
        Long findUserId = 2L;
        Long chattingRoomId = 1L;
        UserRole userRole = UserRole.DOCTOR;
        AuthUser authUser = AuthUser.of(userId, userRole);

        User findUser =  User.of(null, null, null, null, null, null);

        User doctor =  User.of(null, null, null, null, null, null);
        ReflectionTestUtils.setField(doctor, "id", findUserId);

        ChattingRoom findChattingRoom = ChattingRoom.of(null, null, null);
        ReflectionTestUtils.setField(findChattingRoom, "doctor", doctor);

        given(chattingRoomRepository.findChattingRoomWithPatientAndDoctor(chattingRoomId)).willReturn(Optional.of(findChattingRoom));

        given(userRepository.findByDoctorId(userId)).willReturn(Optional.of(findUser));

        // when
        ClientException clientException = assertThrows(ClientException.class, () -> chattingRoomService.findChattingRoomById(authUser, chattingRoomId));

        // then
        assertThat(clientException.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN_CHATTING_ROOM_ACCESS);

        verify(chattingRoomRepository, times(1)).findChattingRoomWithPatientAndDoctor(chattingRoomId);
        verify(userRepository, times(0)).findByPatientId(userId);
        verify(userRepository, times(1)).findByDoctorId(userId);
    }

    @Test
    @DisplayName("채팅방 조회시 채팅방이 비활성화 상태일 때")
    void findChattingRoomByIdNotActive(){
        // given
        Long userId = 1L;
        Long chattingRoomId = 1L;
        UserRole userRole = UserRole.PATIENT;
        AuthUser authUser = AuthUser.of(userId, userRole);
        Boolean isActive = false;

        User findUser =  User.of(null, null, null, null, null, null);

        User patient =  User.of(null, null, null, null, null, null);
        ReflectionTestUtils.setField(patient, "id", userId);

        ChattingRoom findChattingRoom = ChattingRoom.of(null, null, null);
        ReflectionTestUtils.setField(findChattingRoom, "patient", patient);
        ReflectionTestUtils.setField(findChattingRoom, "isActive", isActive);

        given(chattingRoomRepository.findChattingRoomWithPatientAndDoctor(chattingRoomId)).willReturn(Optional.of(findChattingRoom));

        given(userRepository.findByPatientId(userId)).willReturn(Optional.of(findUser));

        // when
        ClientException clientException = assertThrows(ClientException.class, () -> chattingRoomService.findChattingRoomById(authUser, chattingRoomId));

        // then
        assertThat(clientException.getErrorCode()).isEqualTo(ErrorCode.INACTIVE_CHATTING_ROOM);

        verify(chattingRoomRepository, times(1)).findChattingRoomWithPatientAndDoctor(chattingRoomId);
        verify(userRepository, times(1)).findByPatientId(userId);
        verify(userRepository, times(0)).findByDoctorId(userId);
    }

    @Test
    @DisplayName("채팅방 조회 성공 테스트")
    void findChattingRoomByIdSuccess(){
        // given
        Long userId = 1L;
        Long doctorId = 2L;
        Long chattingRoomId = 1L;
        UserRole userRole = UserRole.PATIENT;
        AuthUser authUser = AuthUser.of(userId, userRole);
        Boolean isActive = true;
        LocalDateTime createdAt = LocalDateTime.now();

        User findUser =  User.of(null, null, null, null, null, null);

        User patient =  User.of(null, null, null, null, null, null);
        ReflectionTestUtils.setField(patient, "id", userId);

        User doctor =  User.of(null, null, null, null, null, null);
        ReflectionTestUtils.setField(doctor, "id", doctorId);

        ChattingRoom findChattingRoom = ChattingRoom.of(doctor, patient, isActive);
        ReflectionTestUtils.setField(findChattingRoom, "id", chattingRoomId);
        ReflectionTestUtils.setField(findChattingRoom, "createdAt", createdAt);

        given(chattingRoomRepository.findChattingRoomWithPatientAndDoctor(chattingRoomId)).willReturn(Optional.of(findChattingRoom));

        given(userRepository.findByPatientId(userId)).willReturn(Optional.of(findUser));

        // when
        ChattingRoomSingleResponse chattingRoomSingleResponse = chattingRoomService.findChattingRoomById(authUser, chattingRoomId);

        // then
        assertThat(chattingRoomSingleResponse.getId()).isEqualTo(chattingRoomId);
        assertThat(chattingRoomSingleResponse.getPatientId()).isEqualTo(userId);
        assertThat(chattingRoomSingleResponse.getDoctorId()).isEqualTo(doctorId);
        assertThat(chattingRoomSingleResponse.getCreatedAt()).isEqualTo(createdAt);

        verify(chattingRoomRepository, times(1)).findChattingRoomWithPatientAndDoctor(chattingRoomId);
        verify(userRepository, times(1)).findByPatientId(userId);
        verify(userRepository, times(0)).findByDoctorId(userId);
    }

    @Test
    @DisplayName("채팅방 리스트 조회 권한이 PATIENT 일 때 유저가 존재하지 않음")
    void findAllChattingRoomsPatientNotExist(){
        // given
        Long userId = 1L;
        UserRole userRole = UserRole.PATIENT;
        AuthUser authUser = AuthUser.of(userId, userRole);

        Pageable pageable = PageRequest.of(0, 10);

        given(userRepository.findByPatientId(userId)).willReturn(Optional.empty());

        // when
        ClientException clientException = assertThrows(ClientException.class, () -> chattingRoomService.findAllChattingRooms(authUser, pageable));

        // then
        assertThat(clientException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);

        verify(userRepository, times(1)).findByPatientId(userId);
        verify(userRepository, times(0)).findByDoctorId(userId);
        verify(chattingRoomRepository, times(0)).findPatientsChattingRooms(userId, pageable);
        verify(chattingRoomRepository, times(0)).findDoctorsChattingRooms(userId, pageable);
    }

    @Test
    @DisplayName("채팅방 리스트 조회 권한이 DOCTOR 일 때 유저가 존재하지 않음")
    void findAllChattingRoomsDoctorNotExist(){
        // given
        Long userId = 1L;
        UserRole userRole = UserRole.DOCTOR;
        AuthUser authUser = AuthUser.of(userId, userRole);

        Pageable pageable = PageRequest.of(0, 10);

        given(userRepository.findByDoctorId(userId)).willReturn(Optional.empty());

        // when
        ClientException clientException = assertThrows(ClientException.class, () -> chattingRoomService.findAllChattingRooms(authUser, pageable));

        // then
        assertThat(clientException.getErrorCode()).isEqualTo(ErrorCode.DOCTOR_NOT_FOUND);

        verify(userRepository, times(0)).findByPatientId(userId);
        verify(userRepository, times(1)).findByDoctorId(userId);
        verify(chattingRoomRepository, times(0)).findPatientsChattingRooms(userId, pageable);
        verify(chattingRoomRepository, times(0)).findDoctorsChattingRooms(userId, pageable);
    }

    @Test
    @DisplayName("채팅방 리스트 조회 권한이 PATIENT 일 때")
    void findAllChattingRoomsPatient(){
        // given
        Long userId = 1L;
        Long doctorId = 2L;
        UserRole userRole = UserRole.PATIENT;
        AuthUser authUser = AuthUser.of(userId, userRole);

        User patient = User.of(null, null, null, null, null, null);

        User doctor = User.of(null, null, null, null, null, null);
        ReflectionTestUtils.setField(doctor, "id", doctorId);

        Pageable pageable = PageRequest.of(0, 10);

        List<ChattingRoom> content = new ArrayList<>();
        for(int i=0;i<10;i++){
            ChattingRoom chattingRoom = ChattingRoom.of(null, null, null);
            ReflectionTestUtils.setField(chattingRoom, "doctor", doctor);
            content.add(chattingRoom);
        }

        Page<ChattingRoom> chattingRoomPage = new PageImpl<>(content, pageable, content.size());

        given(userRepository.findByPatientId(userId)).willReturn(Optional.of(patient));

        given(chattingRoomRepository.findPatientsChattingRooms(userId, pageable)).willReturn(chattingRoomPage);

        // when
        PageResult<ChattingRoomListResponse> pageResult = chattingRoomService.findAllChattingRooms(authUser, pageable);

        // then
        List<ChattingRoomListResponse> responseContent = pageResult.getContent();
        PageInfo pageInfo = pageResult.getPageInfo();

        assertThat(responseContent.size()).isEqualTo(content.size());
        assertThat(pageInfo.getPageNum()).isEqualTo(pageable.getPageNumber());
        assertThat(pageInfo.getPageSize()).isEqualTo(pageable.getPageSize());
        assertThat(pageInfo.getTotalPage()).isEqualTo(chattingRoomPage.getTotalPages());
        assertThat(pageInfo.getTotalElement()).isEqualTo(chattingRoomPage.getTotalElements());

        verify(userRepository, times(1)).findByPatientId(userId);
        verify(userRepository, times(0)).findByDoctorId(userId);
        verify(chattingRoomRepository, times(1)).findPatientsChattingRooms(userId, pageable);
        verify(chattingRoomRepository, times(0)).findDoctorsChattingRooms(userId, pageable);
    }

    @Test
    @DisplayName("채팅방 리스트 조회 권한이 DOCTOR 일 때")
    void findAllChattingRoomsDoctor(){
        // given
        Long userId = 1L;
        Long patientId = 2L;
        UserRole userRole = UserRole.DOCTOR;
        AuthUser authUser = AuthUser.of(userId, userRole);

        User doctor = User.of(null, null, null, null, null, null);

        User patient = User.of(null, null, null, null, null, null);
        ReflectionTestUtils.setField(doctor, "id", patientId);

        Pageable pageable = PageRequest.of(0, 10);

        List<ChattingRoom> content = new ArrayList<>();
        for(int i=0;i<10;i++){
            ChattingRoom chattingRoom = ChattingRoom.of(null, null, null);
            ReflectionTestUtils.setField(chattingRoom, "patient", patient);
            content.add(chattingRoom);
        }

        Page<ChattingRoom> chattingRoomPage = new PageImpl<>(content, pageable, content.size());

        given(userRepository.findByDoctorId(userId)).willReturn(Optional.of(doctor));

        given(chattingRoomRepository.findDoctorsChattingRooms(userId, pageable)).willReturn(chattingRoomPage);

        // when
        PageResult<ChattingRoomListResponse> pageResult = chattingRoomService.findAllChattingRooms(authUser, pageable);

        // then
        List<ChattingRoomListResponse> responseContent = pageResult.getContent();
        PageInfo pageInfo = pageResult.getPageInfo();

        assertThat(responseContent.size()).isEqualTo(content.size());
        assertThat(pageInfo.getPageNum()).isEqualTo(pageable.getPageNumber());
        assertThat(pageInfo.getPageSize()).isEqualTo(pageable.getPageSize());
        assertThat(pageInfo.getTotalPage()).isEqualTo(chattingRoomPage.getTotalPages());
        assertThat(pageInfo.getTotalElement()).isEqualTo(chattingRoomPage.getTotalElements());

        verify(userRepository, times(0)).findByPatientId(userId);
        verify(userRepository, times(1)).findByDoctorId(userId);
        verify(chattingRoomRepository, times(0)).findPatientsChattingRooms(userId, pageable);
        verify(chattingRoomRepository, times(1)).findDoctorsChattingRooms(userId, pageable);
    }

    @Test
    @DisplayName("채팅방 비활성화시 존재하지 않는 채팅방일 때")
    void deleteChattingRoomByIdNotFoundChattingRoomTest(){
        // given
        Long userId = 1L;
        UserRole userRole = UserRole.PATIENT;
        AuthUser authUser = AuthUser.of(userId, userRole);

        Long chattingRoomId = 1L;

        given(chattingRoomRepository.findChattingRoomWithPatientAndDoctor(chattingRoomId)).willReturn(Optional.empty());

        // when
        ClientException clientException = assertThrows(ClientException.class, () -> chattingRoomService.deleteChattingRoomById(authUser, chattingRoomId));

        // then
        assertThat(clientException.getErrorCode()).isEqualTo(ErrorCode.CHATTING_ROOM_NOT_FOUND);

        verify(chattingRoomRepository, times(1)).findChattingRoomWithPatientAndDoctor(chattingRoomId);
        verify(userRepository, times(0)).findByPatientId(userId);
        verify(userRepository, times(0)).findByDoctorId(userId);
    }

    @Test
    @DisplayName("채팅방 비활성화시 권한이 없는 환자일 때")
    void deleteChattingRoomByIdInvalidPatientTest(){
        // given
        Long userId = 1L;
        Long findUserId = 2L;
        Long chattingRoomId = 1L;
        UserRole userRole = UserRole.PATIENT;
        AuthUser authUser = AuthUser.of(userId, userRole);

        User findUser = User.of(null, null, null, null, null, null);

        User patient = User.of(null, null, null, null, null, null);
        ReflectionTestUtils.setField(patient, "id", findUserId);

        ChattingRoom findChattingRoom = ChattingRoom.of(null, null, null);
        ReflectionTestUtils.setField(findChattingRoom, "patient", patient);

        given(chattingRoomRepository.findChattingRoomWithPatientAndDoctor(chattingRoomId)).willReturn(Optional.of(findChattingRoom));

        given(userRepository.findByPatientId(userId)).willReturn(Optional.of(findUser));

        // when
        ClientException clientException = assertThrows(ClientException.class, () -> chattingRoomService.deleteChattingRoomById(authUser, chattingRoomId));

        // then
        assertThat(clientException.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN_CHATTING_ROOM_ACCESS);

        verify(chattingRoomRepository, times(1)).findChattingRoomWithPatientAndDoctor(chattingRoomId);
        verify(userRepository, times(1)).findByPatientId(userId);
        verify(userRepository, times(0)).findByDoctorId(userId);
    }

    @Test
    @DisplayName("채팅방 비활성화시 권한이 없는 의사일 때")
    void deleteChattingRoomByIdInvalidDoctorTest(){
        // given
        Long userId = 1L;
        Long findUserId = 2L;
        Long chattingRoomId = 1L;
        UserRole userRole = UserRole.DOCTOR;
        AuthUser authUser = AuthUser.of(userId, userRole);

        User findUser = User.of(null, null, null, null, null, null);

        User doctor = User.of(null, null, null, null, null, null);
        ReflectionTestUtils.setField(doctor, "id", findUserId);

        ChattingRoom findChattingRoom = ChattingRoom.of(null, null, null);
        ReflectionTestUtils.setField(findChattingRoom, "doctor", doctor);

        given(chattingRoomRepository.findChattingRoomWithPatientAndDoctor(chattingRoomId)).willReturn(Optional.of(findChattingRoom));

        given(userRepository.findByDoctorId(userId)).willReturn(Optional.of(findUser));

        // when
        ClientException clientException = assertThrows(ClientException.class, () -> chattingRoomService.deleteChattingRoomById(authUser, chattingRoomId));

        // then
        assertThat(clientException.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN_CHATTING_ROOM_ACCESS);

        verify(chattingRoomRepository, times(1)).findChattingRoomWithPatientAndDoctor(chattingRoomId);
        verify(userRepository, times(0)).findByPatientId(userId);
        verify(userRepository, times(1)).findByDoctorId(userId);
    }

    @Test
    @DisplayName("채팅방 비활성화시 이미 비활성화 된 채팅방일 때 테스트")
    void deleteChattingRoomByIdNotActiveTest(){
        // given
        Long userId = 1L;
        Long findUserId = 1L;
        Long chattingRoomId = 1L;
        UserRole userRole = UserRole.PATIENT;
        AuthUser authUser = AuthUser.of(userId, userRole);

        User findUser = User.of(null, null, null, null, null, null);

        User patient = User.of(null, null, null, null, null, null);
        ReflectionTestUtils.setField(patient, "id", findUserId);

        ChattingRoom findChattingRoom = ChattingRoom.of(null, null, null);
        ReflectionTestUtils.setField(findChattingRoom, "patient", patient);
        ReflectionTestUtils.setField(findChattingRoom, "isActive", false);

        given(chattingRoomRepository.findChattingRoomWithPatientAndDoctor(chattingRoomId)).willReturn(Optional.of(findChattingRoom));

        given(userRepository.findByPatientId(userId)).willReturn(Optional.of(findUser));

        // when
        ClientException clientException = assertThrows(ClientException.class, () -> chattingRoomService.deleteChattingRoomById(authUser, chattingRoomId));

        // then
        assertThat(clientException.getErrorCode()).isEqualTo(ErrorCode.INACTIVE_CHATTING_ROOM);

        verify(chattingRoomRepository, times(1)).findChattingRoomWithPatientAndDoctor(chattingRoomId);
        verify(userRepository, times(1)).findByPatientId(userId);
        verify(userRepository, times(0)).findByDoctorId(userId);
    }

    @Test
    @DisplayName("채팅방 비활성화시 성공 테스트")
    void deleteChattingRoomByIdTest(){
        // given
        Long userId = 1L;
        Long findUserId = 1L;
        Long chattingRoomId = 1L;
        UserRole userRole = UserRole.PATIENT;
        AuthUser authUser = AuthUser.of(userId, userRole);

        User findUser = User.of(null, null, null, null, null, null);

        User patient = User.of(null, null, null, null, null, null);
        ReflectionTestUtils.setField(patient, "id", findUserId);

        ChattingRoom findChattingRoom = ChattingRoom.of(null, null, null);
        ReflectionTestUtils.setField(findChattingRoom, "patient", patient);
        ReflectionTestUtils.setField(findChattingRoom, "isActive", true);

        given(chattingRoomRepository.findChattingRoomWithPatientAndDoctor(chattingRoomId)).willReturn(Optional.of(findChattingRoom));

        given(userRepository.findByPatientId(userId)).willReturn(Optional.of(findUser));

        // when
        chattingRoomService.deleteChattingRoomById(authUser, chattingRoomId);

        // then
        assertThat(findChattingRoom.getIsActive()).isFalse();

        verify(chattingRoomRepository, times(1)).findChattingRoomWithPatientAndDoctor(chattingRoomId);
        verify(userRepository, times(1)).findByPatientId(userId);
        verify(userRepository, times(0)).findByDoctorId(userId);
    }
}