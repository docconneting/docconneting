package com.example.docconneting.domain.chatting.controller;

import com.example.docconneting.common.response.PageResult;
import com.example.docconneting.common.response.Response;
import com.example.docconneting.domain.auth.annotation.Auth;
import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.chatting.dto.ChattingRoomCreateResponse;
import com.example.docconneting.domain.chatting.dto.ChattingRoomListResponse;
import com.example.docconneting.domain.chatting.dto.ChattingRoomSingleResponse;
import com.example.docconneting.domain.chatting.service.ChattingRoomService;
import io.lettuce.core.GeoArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChattingRoomController {

    private final ChattingRoomService chattingRoomService;

    @MessageMapping("/chattingRooms/{chattingRoomId}")
    @SendTo("/sub/chattingRooms/{chattingRoomId}")
    public String sendMessage(@DestinationVariable Long chattingRoomId) throws Exception {
        return HtmlUtils.htmlEscape(String.valueOf(chattingRoomId)) + "번 채팅 방";
    }

    @PostMapping("/api/v1/doctors/{doctorId}/chattingRooms")
    public ResponseEntity<Response<ChattingRoomCreateResponse>> createdChattingRoom(@Auth AuthUser authUser, @PathVariable Long doctorId){
        return ResponseEntity
                .ok()
                .body(Response.of(chattingRoomService.createdChattingRoom(authUser, doctorId)));
    }

    @GetMapping("/api/v1/chattingRooms/{chattingRoomId}")
    public ResponseEntity<Response<ChattingRoomSingleResponse>> findChattingRoomById(@Auth AuthUser authUser, @PathVariable Long chattingRoomId){
        return ResponseEntity
                .ok()
                .body(Response.of(chattingRoomService.findChattingRoomById(authUser, chattingRoomId)));
    }

    @GetMapping("/api/v1/chattingRooms")
    public ResponseEntity<Response<List<ChattingRoomListResponse>>> findAllChattingRooms(@Auth AuthUser authUser, @PageableDefault(sort="createdAt", direction = Sort.Direction.DESC) Pageable pageable){

        PageResult<ChattingRoomListResponse> chattingRooms = chattingRoomService.findAllChattingRooms(authUser, pageable);

        return ResponseEntity
                .ok()
                .body(Response.of(chattingRooms.getContent(), chattingRooms.getPageInfo()));
    }
}
