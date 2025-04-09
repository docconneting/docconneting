package com.example.docconneting.domain.chatting.controller;

import com.example.docconneting.common.response.Response;
import com.example.docconneting.domain.auth.annotation.Auth;
import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.chatting.dto.ChattingRoomCreateResponse;
import com.example.docconneting.domain.chatting.dto.ChattingRoomSingleResponse;
import com.example.docconneting.domain.chatting.service.ChattingRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.util.HtmlUtils;

@Controller
@RequiredArgsConstructor
public class ChattingRoomController {

    private final ChattingRoomService chattingRoomService;

    @MessageMapping("/rooms/{roomId}")
    @SendTo("/sub/rooms/{roomId}")
    public String sendMessage(@DestinationVariable Long roomId) throws Exception {
        return HtmlUtils.htmlEscape(String.valueOf(roomId)) + "번 채팅 방";
    }

    @PostMapping("/api/v1/doctors/{doctorId}/chattingRooms")
    public ResponseEntity<Response<ChattingRoomCreateResponse>> createdChattingRoom(@Auth AuthUser authUser, @PathVariable Long doctorId){
        return ResponseEntity
                .ok()
                .body(Response.of(chattingRoomService.createdChattingRoom(authUser, doctorId)));
    }

    @PostMapping("/api/v1/chattingRooms/{chattingRoomId}")
    public ResponseEntity<Response<ChattingRoomSingleResponse>> findChattingRoomById(@Auth AuthUser authUser, @PathVariable Long chattingRoomId){
        return ResponseEntity
                .ok()
                .body(Response.of(chattingRoomService.findChattingRoomById(authUser, chattingRoomId)));
    }
}
