package com.example.docconneting.domain.chatting.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChattingRoomSingleResponse {
    private final Long id;

    private final Long patientId;

    private final Long doctorId;

    private final LocalDateTime createdAt;

    private ChattingRoomSingleResponse(Long id, Long patientId, Long doctorId, LocalDateTime createdAt) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.createdAt = createdAt;
    }

    public static ChattingRoomSingleResponse of(Long id, Long patientId, Long doctorId, LocalDateTime createdAt){
        return new ChattingRoomSingleResponse(id, patientId, doctorId, createdAt);
    }
}
