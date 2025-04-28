package com.example.docconneting.domain.chatting.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MessageQueuePayload {

    private final Long chattingRoomId;

    private final Long userId;

    private final String contents;

    private final LocalDateTime createdAt;

    private MessageQueuePayload(Long chattingRoomId, Long userId, String contents, LocalDateTime createdAt) {
        this.chattingRoomId = chattingRoomId;
        this.userId = userId;
        this.contents = contents;
        this.createdAt = createdAt;
    }

    public static MessageQueuePayload of(Long chattingRoomId, Long userId, String contents, LocalDateTime createdAt){
        return new MessageQueuePayload(chattingRoomId, userId, contents, createdAt);
    }
}
