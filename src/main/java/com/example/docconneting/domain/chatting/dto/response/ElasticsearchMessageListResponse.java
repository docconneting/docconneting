package com.example.docconneting.domain.chatting.dto.response;

import com.example.docconneting.domain.chatting.dto.projection.MessageList;
import com.example.docconneting.domain.chatting.entity.ElasticsearchMessage;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ElasticsearchMessageListResponse {
    private final Long userId;

    private final String contents;

    private final Instant createdAt;

    private ElasticsearchMessageListResponse(Long userId, String contents, Instant createdAt) {
        this.userId = userId;
        this.contents = contents;
        this.createdAt = createdAt;
    }

    public static List<ElasticsearchMessageListResponse> toMessageListResponses(List<ElasticsearchMessage> messages){
        return messages.stream().map(message ->
                        new ElasticsearchMessageListResponse(
                                message.getUserId(),
                                message.getContents(),
                                message.getCreatedAt())
                )
                .collect(Collectors.toList());
    }
}
