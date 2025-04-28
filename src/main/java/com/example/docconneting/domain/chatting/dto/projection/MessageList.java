package com.example.docconneting.domain.chatting.dto.projection;

import java.time.LocalDateTime;

public interface MessageList {
    Long getUserId();
    String getContents();
    LocalDateTime getCreatedAt();
}
