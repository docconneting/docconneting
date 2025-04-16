package com.example.docconneting.domain.chatting.service;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.common.response.PageInfo;
import com.example.docconneting.common.response.PageResult;
import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.chatting.dto.request.MessageRequest;
import com.example.docconneting.domain.chatting.dto.response.ChattingRoomListResponse;
import com.example.docconneting.domain.chatting.dto.response.MessageListResponse;
import com.example.docconneting.domain.chatting.dto.response.MessageResponse;
import com.example.docconneting.domain.chatting.entity.ChattingRoom;
import com.example.docconneting.domain.chatting.entity.Message;
import com.example.docconneting.domain.chatting.repository.ChattingRoomRepository;
import com.example.docconneting.domain.chatting.repository.MessageRepository;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.enums.UserRole;
import com.example.docconneting.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChattingRoomRepository chattingRoomRepository;

    @Transactional
    public MessageResponse createMessage(MessageRequest messageRequest, Long userId, Long chattingRoomId){

        // 채팅방에 들어올 삭제된 유저인지 여부를 확인 하므로 유저만 찾아오도록 함
        User findUser = userRepository.findById(userId).orElseThrow(() -> new ClientException(ErrorCode.USER_NOT_FOUND));

        // 채팅방에 들어올 때 비활성화 된 채팅방인지 여부를 확인 하므로 채팅방만 찾아오도록 함
        ChattingRoom findChattingRoom = chattingRoomRepository.findById(chattingRoomId).orElseThrow(() -> new ClientException(ErrorCode.CHATTING_ROOM_NOT_FOUND));

        Message message = Message.of(findUser, findChattingRoom, messageRequest.getContents());

        Message savedMessage = messageRepository.save(message);

        return MessageResponse.of(userId, findUser.getUsername(), savedMessage.getContents(), savedMessage.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public PageResult<MessageListResponse> findAllMessages(AuthUser authUser, Long chattingRoomId, Pageable pageable){

        ChattingRoom findChattingRoom = chattingRoomRepository.findById(chattingRoomId).orElseThrow(() -> new ClientException(ErrorCode.CHATTING_ROOM_NOT_FOUND));

        if (UserRole.PATIENT.equals(authUser.getUserRole())){

            userRepository.findByPatientId(authUser.getId()).orElseThrow(() -> new ClientException(ErrorCode.USER_NOT_FOUND));

            if (authUser.getId() != findChattingRoom.getPatient().getId()){
                throw new ClientException(ErrorCode.FORBIDDEN_CHATTING_ROOM_ACCESS);
            }

        }
        else if (UserRole.DOCTOR.equals(authUser.getUserRole())){

            userRepository.findByDoctorId(authUser.getId()).orElseThrow(() -> new ClientException(ErrorCode.DOCTOR_NOT_FOUND));

            if (authUser.getId() != findChattingRoom.getDoctor().getId()){
                throw new ClientException(ErrorCode.FORBIDDEN_CHATTING_ROOM_ACCESS);
            }

        }

        if (!findChattingRoom.getIsActive()){

            throw new ClientException(ErrorCode.INACTIVE_CHATTING_ROOM);

        }

        Page<Message> messages = messageRepository.findAllMessagesWithUser(chattingRoomId, pageable);

        List<Message> content = messages.getContent();
        Pageable messagesPageable = messages.getPageable();

        List<MessageListResponse> messageListResponses = MessageListResponse.toMessageListResponses(content);

        PageInfo pageInfo = PageInfo.builder()
                .pageNum(messagesPageable.getPageNumber())
                .pageSize(messagesPageable.getPageSize())
                .totalElement(messages.getTotalElements())
                .totalPage(messages.getTotalPages())
                .build();

        return new PageResult<>(messageListResponses, pageInfo);
    }

}
