package com.example.docconneting.domain.chatting.service;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.domain.chatting.dto.request.MessageRequest;
import com.example.docconneting.domain.chatting.dto.response.MessageResponse;
import com.example.docconneting.domain.chatting.entity.ChattingRoom;
import com.example.docconneting.domain.chatting.entity.Message;
import com.example.docconneting.domain.chatting.repository.ChattingRoomRepository;
import com.example.docconneting.domain.chatting.repository.MessageRepository;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}
