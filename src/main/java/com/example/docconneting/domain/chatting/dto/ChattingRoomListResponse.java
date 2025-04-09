package com.example.docconneting.domain.chatting.dto;

import com.example.docconneting.domain.chatting.entity.ChattingRoom;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ChattingRoomListResponse {

    private final Long id;

    private final String opponentName;

    private final LocalDateTime createdAt;

    private ChattingRoomListResponse(Long id, String opponentName, LocalDateTime createdAt){
        this.id = id;
        this.opponentName = opponentName;
        this.createdAt = createdAt;
    }

    public static List<ChattingRoomListResponse> toChattingRoomListResponsesForPatient(List<ChattingRoom> chattingRooms) {
        return chattingRooms.stream().map(chattingRoom ->
                        new ChattingRoomListResponse(
                                chattingRoom.getId(),
                                chattingRoom.getDoctor().getUsername(),
                                chattingRoom.getCreatedAt())
                )
                .collect(Collectors.toList());
    }

    public static List<ChattingRoomListResponse> toChattingRoomListResponsesForDoctor(List<ChattingRoom> chattingRooms) {
        return chattingRooms.stream().map(chattingRoom ->
                        new ChattingRoomListResponse(
                                chattingRoom.getId(),
                                chattingRoom.getPatient().getUsername(),
                                chattingRoom.getCreatedAt())
                )
                .collect(Collectors.toList());
    }

}
