package com.example.docconneting.domain.chatting.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChattingRoomCreateResponse {
    private final Long id;

    private final Long patientId;

    private final Long doctorId;

    private final Boolean isRecovered;

    private final LocalDateTime createdAt;

    private ChattingRoomCreateResponse(Long id, Long patientId, Long doctorId, Boolean isRecovered, LocalDateTime createdAt) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.isRecovered = isRecovered;
        this.createdAt = createdAt;
    }

    public static ChattingRoomCreateResponse of(Long id, Long patientId, Long doctorId, Boolean isRecovered, LocalDateTime createdAt){
        return new ChattingRoomCreateResponse(id, patientId, doctorId, isRecovered, createdAt);
    }
}
