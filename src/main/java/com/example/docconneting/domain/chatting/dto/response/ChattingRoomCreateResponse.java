package com.example.docconneting.domain.chatting.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChattingRoomCreateResponse {
    private final Long id;

    private final Long patientId;

    private final String patientName;

    private final Long doctorId;

    private final String doctorName;

    private final Boolean isRecovered;

    private final LocalDateTime createdAt;

    private ChattingRoomCreateResponse(Long id, Long patientId, String patientName, Long doctorId, String doctorName, Boolean isRecovered, LocalDateTime createdAt) {
        this.id = id;
        this.patientId = patientId;
        this.patientName = patientName;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.isRecovered = isRecovered;
        this.createdAt = createdAt;
    }

    public static ChattingRoomCreateResponse of(Long id, Long patientId, String patientName, Long doctorId, String doctorName, Boolean isRecovered, LocalDateTime createdAt){
        return new ChattingRoomCreateResponse(id, patientId, patientName, doctorId, doctorName, isRecovered, createdAt);
    }
}
