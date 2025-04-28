package com.example.docconneting.domain.chatting.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChattingRoomSingleResponse {
    private final Long id;

    private final Long patientId;

    private final String patientName;

    private final Long doctorId;

    private final String doctorName;

    private final LocalDateTime createdAt;

    private ChattingRoomSingleResponse(Long id, Long patientId, String patientName, Long doctorId, String doctorName, LocalDateTime createdAt) {
        this.id = id;
        this.patientId = patientId;
        this.patientName = patientName;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.createdAt = createdAt;
    }

    public static ChattingRoomSingleResponse of(Long id, Long patientId, String patientName, Long doctorId, String doctorName, LocalDateTime createdAt){
        return new ChattingRoomSingleResponse(id, patientId, patientName, doctorId, doctorName, createdAt);
    }
}
