package com.example.docconneting.domain.chatting.controller;

import com.example.docconneting.common.response.PageResult;
import com.example.docconneting.common.response.Response;
import com.example.docconneting.domain.auth.annotation.Auth;
import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.chatting.dto.response.ChattingRoomCreateResponse;
import com.example.docconneting.domain.chatting.dto.response.ChattingRoomListResponse;
import com.example.docconneting.domain.chatting.dto.response.ChattingRoomSingleResponse;
import com.example.docconneting.domain.chatting.service.ChattingRoomService;
import com.example.docconneting.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ChattingRoomController {

    private final ChattingRoomService chattingRoomService;

    @PostMapping("/doctors/{doctorId}/chattingRooms")
    public ResponseEntity<Response<ChattingRoomCreateResponse>> createdChattingRoom(@Auth AuthUser authUser, @PathVariable Long doctorId){
        return ResponseEntity
                .ok()
                .body(Response.of(chattingRoomService.createdChattingRoom(authUser, doctorId)));
    }

    @GetMapping("/chattingRooms/{chattingRoomId}")
    public ResponseEntity<Response<ChattingRoomSingleResponse>> findChattingRoomById(@Auth AuthUser authUser, @PathVariable Long chattingRoomId){
        return ResponseEntity
                .ok()
                .body(Response.of(chattingRoomService.findChattingRoomById(authUser, chattingRoomId)));
    }

    @GetMapping("/chattingRooms")
    public ResponseEntity<Response<List<ChattingRoomListResponse>>> findAllChattingRooms(@Auth AuthUser authUser, @PageableDefault(sort="createdAt", direction = Sort.Direction.DESC) Pageable pageable){

        PageResult<ChattingRoomListResponse> chattingRooms = chattingRoomService.findAllChattingRooms(authUser, pageable);

        return ResponseEntity
                .ok()
                .body(Response.of(chattingRooms.getContent(), chattingRooms.getPageInfo()));
    }

    @PostMapping("/chattingRooms/{chattingRoomId}")
    public ResponseEntity<Response<Map<String, String>>> deletedChattingRoomById(@Auth AuthUser authUser, @PathVariable Long chattingRoomId){

        Map<String, String> message = new HashMap<>();
        message.put("message", "채팅방이 성공적으로 비활성화 되었습니다.");

        chattingRoomService.deleteChattingRoomById(authUser, chattingRoomId);

        return ResponseEntity
                .ok()
                .body(Response.of(message));
    }
}
