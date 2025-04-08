package com.example.docconneting.domain.chatting.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class ChattingRoomController {
    @MessageMapping("/rooms/{roomId}")
    @SendTo("/sub/rooms/{roomId}")
    public String sendMessage(@DestinationVariable Long roomId) throws Exception {
        return HtmlUtils.htmlEscape(String.valueOf(roomId)) + "번 채팅 방";
    }
}
