package com.example.docconneting.domain.chatting.service;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.common.response.PageInfo;
import com.example.docconneting.common.response.PageResult;
import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.chatting.dto.projection.MessageList;
import com.example.docconneting.domain.chatting.dto.response.MessageListResponse;
import com.example.docconneting.domain.chatting.entity.ChattingRoom;
import com.example.docconneting.domain.chatting.repository.ChattingRoomRepository;
import com.example.docconneting.domain.chatting.repository.MessageRepository;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class MessageServiceTest {
    @Mock
    MessageRepository messageRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ChattingRoomRepository chattingRoomRepository;

    @InjectMocks
    MessageService messageService;

    @Test
    @DisplayName("메시지 리스트 조회 존재하지 않는 채팅방일 때")
    void findAllMessagesChattingRoomNotExist(){
        // given
        Long userId = 1L;
        UserRole userRole = UserRole.PATIENT;
        AuthUser authUser = AuthUser.of(userId, userRole);

        Long chattingRoomId = 1L;

        Pageable pageable = PageRequest.of(0,10);

        given(chattingRoomRepository.findById(chattingRoomId)).willReturn(Optional.empty());

        // when
        ClientException clientException = assertThrows(ClientException.class, () -> messageService.findAllMessages(authUser, chattingRoomId, pageable));

        // then
        assertThat(clientException.getErrorCode()).isEqualTo(ErrorCode.CHATTING_ROOM_NOT_FOUND);

        verify(chattingRoomRepository, times(1)).findById(chattingRoomId);
        verify(userRepository, times(0)).findByPatientId(userId);
        verify(userRepository, times(0)).findByDoctorId(userId);
        verify(messageRepository, times(0)).findAllMessages(chattingRoomId, pageable);
    }

    @Test
    @DisplayName("메시지 리스트 조회 존재하지 않는 환자일 때")
    void findAllMessagesPatientNotExist(){
        // given
        Long userId = 1L;
        UserRole userRole = UserRole.PATIENT;
        AuthUser authUser = AuthUser.of(userId, userRole);

        Long chattingRoomId = 1L;

        Pageable pageable = PageRequest.of(0,10);

        ChattingRoom chattingRoom = ChattingRoom.of(null, null, null);

        given(chattingRoomRepository.findById(userId)).willReturn(Optional.of(chattingRoom));

        given(userRepository.findByPatientId(userId)).willReturn(Optional.empty());

        // when
        ClientException clientException = assertThrows(ClientException.class, () -> messageService.findAllMessages(authUser, chattingRoomId, pageable));

        // then
        assertThat(clientException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);

        verify(chattingRoomRepository, times(1)).findById(chattingRoomId);
        verify(userRepository, times(1)).findByPatientId(userId);
        verify(userRepository, times(0)).findByDoctorId(userId);
        verify(messageRepository, times(0)).findAllMessages(chattingRoomId, pageable);
    }

    @Test
    @DisplayName("메시지 리스트 조회 존재하지 않는 의사일 때")
    void findAllMessagesDoctorNotExist(){
        // given
        Long userId = 1L;
        UserRole userRole = UserRole.DOCTOR;
        AuthUser authUser = AuthUser.of(userId, userRole);

        Long chattingRoomId = 1L;

        Pageable pageable = PageRequest.of(0,10);

        ChattingRoom chattingRoom = ChattingRoom.of(null, null, null);

        given(chattingRoomRepository.findById(userId)).willReturn(Optional.of(chattingRoom));

        given(userRepository.findByDoctorId(userId)).willReturn(Optional.empty());

        // when
        ClientException clientException = assertThrows(ClientException.class, () -> messageService.findAllMessages(authUser, chattingRoomId, pageable));

        // then
        assertThat(clientException.getErrorCode()).isEqualTo(ErrorCode.DOCTOR_NOT_FOUND);

        verify(chattingRoomRepository, times(1)).findById(chattingRoomId);
        verify(userRepository, times(0)).findByPatientId(userId);
        verify(userRepository, times(1)).findByDoctorId(userId);
        verify(messageRepository, times(0)).findAllMessages(chattingRoomId, pageable);
    }

    @Test
    @DisplayName("메시지 리스트 조회 권한이 없는 환자일 경우")
    void findAllMessagesInvalidPatient(){
        // given
        Long userId = 1L;
        Long findUserId = 2L;
        UserRole userRole = UserRole.PATIENT;
        AuthUser authUser = AuthUser.of(userId, userRole);

        Long chattingRoomId = 1L;

        Pageable pageable = PageRequest.of(0,10);

        User findUser = User.of(null, null, null, null, null, null);

        User patient = User.of(null, null, null, null, null, null);
        ReflectionTestUtils.setField(patient, "id", findUserId);

        ChattingRoom chattingRoom = ChattingRoom.of(null, null, null);
        ReflectionTestUtils.setField(chattingRoom, "patient", patient);

        given(chattingRoomRepository.findById(userId)).willReturn(Optional.of(chattingRoom));

        given(userRepository.findByPatientId(userId)).willReturn(Optional.of(findUser));

        // when
        ClientException clientException = assertThrows(ClientException.class, () -> messageService.findAllMessages(authUser, chattingRoomId, pageable));

        // then
        assertThat(clientException.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN_CHATTING_ROOM_ACCESS);

        verify(chattingRoomRepository, times(1)).findById(chattingRoomId);
        verify(userRepository, times(1)).findByPatientId(userId);
        verify(userRepository, times(0)).findByDoctorId(userId);
        verify(messageRepository, times(0)).findAllMessages(chattingRoomId, pageable);
    }

    @Test
    @DisplayName("메시지 리스트 조회 의사일 때 권한이 없는 경우")
    void findAllMessagesInvalidDoctor(){
        // given
        Long userId = 1L;
        Long findUserId = 2L;
        UserRole userRole = UserRole.DOCTOR;
        AuthUser authUser = AuthUser.of(userId, userRole);

        Long chattingRoomId = 1L;

        Pageable pageable = PageRequest.of(0,10);

        User findUser = User.of(null, null, null, null, null, null);

        User doctor = User.of(null, null, null, null, null, null);
        ReflectionTestUtils.setField(doctor, "id", findUserId);

        ChattingRoom chattingRoom = ChattingRoom.of(null, null, null);
        ReflectionTestUtils.setField(chattingRoom, "doctor", doctor);

        given(chattingRoomRepository.findById(userId)).willReturn(Optional.of(chattingRoom));

        given(userRepository.findByDoctorId(userId)).willReturn(Optional.of(findUser));

        // when
        ClientException clientException = assertThrows(ClientException.class, () -> messageService.findAllMessages(authUser, chattingRoomId, pageable));

        // then
        assertThat(clientException.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN_CHATTING_ROOM_ACCESS);

        verify(chattingRoomRepository, times(1)).findById(chattingRoomId);
        verify(userRepository, times(0)).findByPatientId(userId);
        verify(userRepository, times(1)).findByDoctorId(userId);
        verify(messageRepository, times(0)).findAllMessages(chattingRoomId, pageable);
    }

    @Test
    @DisplayName("메시지 리스트 조회 비활성화된 채팅방인 경우")
    void findAllMessagesNotActiveChattingRoom(){
        // given
        Long userId = 1L;
        Long findUserId = 1L;
        UserRole userRole = UserRole.PATIENT;
        AuthUser authUser = AuthUser.of(userId, userRole);

        Long chattingRoomId = 1L;

        Pageable pageable = PageRequest.of(0,10);

        User findUser = User.of(null, null, null, null, null, null);

        User patient = User.of(null, null, null, null, null, null);
        ReflectionTestUtils.setField(patient, "id", findUserId);

        ChattingRoom chattingRoom = ChattingRoom.of(null, null, null);
        ReflectionTestUtils.setField(chattingRoom, "patient", patient);
        ReflectionTestUtils.setField(chattingRoom, "isActive", false);

        given(chattingRoomRepository.findById(userId)).willReturn(Optional.of(chattingRoom));

        given(userRepository.findByPatientId(userId)).willReturn(Optional.of(findUser));

        // when
        ClientException clientException = assertThrows(ClientException.class, () -> messageService.findAllMessages(authUser, chattingRoomId, pageable));

        // then
        assertThat(clientException.getErrorCode()).isEqualTo(ErrorCode.INACTIVE_CHATTING_ROOM);

        verify(chattingRoomRepository, times(1)).findById(chattingRoomId);
        verify(userRepository, times(1)).findByPatientId(userId);
        verify(userRepository, times(0)).findByDoctorId(userId);
        verify(messageRepository, times(0)).findAllMessages(chattingRoomId, pageable);
    }

    @Test
    @DisplayName("메시지 리스트 조회 성공")
    void findAllMessagesTest(){
        // given
        Long userId = 1L;
        Long findUserId = 1L;
        UserRole userRole = UserRole.PATIENT;
        AuthUser authUser = AuthUser.of(userId, userRole);

        Long chattingRoomId = 1L;

        Pageable pageable = PageRequest.of(0,10);

        User findUser = User.of(null, null, null, null, null, null);

        User patient = User.of(null, null, null, null, null, null);
        ReflectionTestUtils.setField(patient, "id", findUserId);

        ChattingRoom chattingRoom = ChattingRoom.of(null, null, null);
        ReflectionTestUtils.setField(chattingRoom, "patient", patient);
        ReflectionTestUtils.setField(chattingRoom, "isActive", true);

        List<MessageList> content = new ArrayList<>();
        for(int i=0;i<10;i++){
            MessageList message = new FakeMessageList(null, null, null);
            content.add(message);
        }

        Page<MessageList> chattingRoomPage = new PageImpl<>(content, pageable, content.size());

        given(chattingRoomRepository.findById(userId)).willReturn(Optional.of(chattingRoom));

        given(userRepository.findByPatientId(userId)).willReturn(Optional.of(findUser));

        given(messageRepository.findAllMessages(chattingRoomId, pageable)).willReturn(chattingRoomPage);

        // when
        PageResult<MessageListResponse> pageResult = messageService.findAllMessages(authUser, chattingRoomId, pageable);

        // then
        List<MessageListResponse> responseContent = pageResult.getContent();
        PageInfo pageInfo = pageResult.getPageInfo();

        assertThat(responseContent.size()).isEqualTo(content.size());
        assertThat(pageInfo.getPageNum()).isEqualTo(pageable.getPageNumber());
        assertThat(pageInfo.getPageSize()).isEqualTo(pageable.getPageSize());
        assertThat(pageInfo.getTotalPage()).isEqualTo(chattingRoomPage.getTotalPages());
        assertThat(pageInfo.getTotalElement()).isEqualTo(chattingRoomPage.getTotalElements());

        verify(chattingRoomRepository, times(1)).findById(chattingRoomId);
        verify(userRepository, times(1)).findByPatientId(userId);
        verify(userRepository, times(0)).findByDoctorId(userId);
        verify(messageRepository, times(1)).findAllMessages(chattingRoomId, pageable);
    }

    static class FakeMessageList implements MessageList {
        private final Long userId;
        private final String contents;
        private final LocalDateTime createdAt;

        public FakeMessageList(Long userId, String contents, LocalDateTime createdAt) {
            this.userId = userId;
            this.contents = contents;
            this.createdAt = createdAt;
        }

        @Override
        public Long getUserId() {
            return userId;
        }

        @Override
        public String getContents() {
            return contents;
        }

        @Override
        public LocalDateTime getCreatedAt() {
            return createdAt;
        }
    }
}