package com.example.docconneting.domain.chatting.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MessageResponse {

    private final Long userId;

    private final String username;

    private final String contents;

    private final LocalDateTime createdAt;

    private MessageResponse(Long userId, String username, String contents, LocalDateTime createdAt) {
        this.userId = userId;
        this.username = username;
        this.contents = contents;
        this.createdAt = createdAt;
    }

    public static MessageResponse of(Long userId, String username, String contents, LocalDateTime createdAt){
        return new MessageResponse(userId, username, contents, createdAt);
    }
}
