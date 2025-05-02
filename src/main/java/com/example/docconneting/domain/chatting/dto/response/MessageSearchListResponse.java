package com.example.docconneting.domain.chatting.dto.response;

import com.example.docconneting.domain.chatting.dto.projection.MessageList;
import com.example.docconneting.domain.chatting.entity.Message;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class MessageSearchListResponse {
    private final Long userId;

    private final String contents;

    private final LocalDateTime createdAt;

    private MessageSearchListResponse(Long userId, String contents, LocalDateTime createdAt) {
        this.userId = userId;
        this.contents = contents;
        this.createdAt = createdAt;
    }

    public static List<MessageSearchListResponse> toMessageListResponses(List<MessageList> messages){
        return messages.stream().map(message ->
                        new MessageSearchListResponse(
                                message.getUserId(),
                                message.getContents(),
                                message.getCreatedAt())
                )
                .collect(Collectors.toList());
    }
}
