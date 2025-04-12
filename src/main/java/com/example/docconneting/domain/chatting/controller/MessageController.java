package com.example.docconneting.domain.chatting.controller;

import com.example.docconneting.domain.chatting.dto.request.MessageRequest;
import com.example.docconneting.domain.chatting.dto.response.MessageResponse;
import com.example.docconneting.domain.chatting.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @MessageMapping("/chattingRooms/{chattingRoomId}")
    @SendTo("/sub/chattingRooms/{chattingRoomId}")
    public MessageResponse sendMessage(@DestinationVariable Long chattingRoomId,
                                       @Payload MessageRequest messageRequest,
                                       Message<?> message) throws Exception {

        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(message);
        Long userId = (Long) stompHeaderAccessor.getSessionAttributes().get("userId");

        return messageService.createMessage(messageRequest, userId, chattingRoomId);
    }
}
