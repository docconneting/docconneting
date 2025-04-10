package com.example.docconneting.domain.chatting.controller;

import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {
    @MessageMapping("/chattingRooms/{chattingRoomId}")
    @SendTo("/sub/chattingRooms/{chattingRoomId}")
    public String sendMessage(@DestinationVariable Long chattingRoomId, Message<?> message) throws Exception {
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(message);
        Long userId = (Long) stompHeaderAccessor.getSessionAttributes().get("userId");
        return userId + "유저";
    }
}
