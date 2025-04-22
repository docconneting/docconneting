package com.example.docconneting.domain.chatting.controller;

import com.example.docconneting.common.response.PageResult;
import com.example.docconneting.common.response.Response;
import com.example.docconneting.domain.auth.annotation.Auth;
import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.chatting.dto.request.MessageRequest;
import com.example.docconneting.domain.chatting.dto.response.ChattingRoomListResponse;
import com.example.docconneting.domain.chatting.dto.response.MessageListResponse;
import com.example.docconneting.domain.chatting.dto.response.MessageResponse;
import com.example.docconneting.domain.chatting.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @MessageMapping("/chattingRooms/{chattingRoomId}")
    public void sendMessage(@DestinationVariable Long chattingRoomId,
                                       @Payload MessageRequest messageRequest,
                                       Message<?> message) throws Exception {

        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(message);
        Long userId = Long.parseLong(stompHeaderAccessor.getUser().getName());

        messageService.createMessage(messageRequest, userId, chattingRoomId);
    }
    
    @GetMapping("/api/v1/chattingRooms/{chattingRoomId}/messages")
    public ResponseEntity<Response<List<MessageListResponse>>> findAllMessages(@Auth AuthUser authUser,
                                                                               @PageableDefault(page = 0, size = 10) Pageable pageable,
                                                                               @PathVariable Long chattingRoomId){
        PageResult<MessageListResponse> messages = messageService.findAllMessages(authUser, chattingRoomId, pageable);

        return ResponseEntity
                .ok()
                .body(Response.of(messages.getContent(), messages.getPageInfo()));
    }
}
