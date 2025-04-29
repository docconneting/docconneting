package com.example.docconneting.domain.chatting.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.Instant;

@Getter
@Document(indexName = "message_index")
@Setting(settingPath = "message/message-setting.json")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ElasticsearchMessage {
    @Id
    private String id;

    @Field(name = "user_id", type = FieldType.Long)
    private Long userId;

    @Field(name = "chatting_room_id", type = FieldType.Long)
    private Long chattingRoomId;

    @Field(name = "contents", type = FieldType.Text, analyzer = "message_analyzer")
    private String contents;

    @CreatedDate
    @Field(name = "created_at", type = FieldType.Date, format = DateFormat.date_optional_time)
    private Instant createdAt;

    private ElasticsearchMessage(Long userId, Long chattingRoomId, String contents) {
        this.userId = userId;
        this.chattingRoomId = chattingRoomId;
        this.contents = contents;
    }

    public static ElasticsearchMessage of(Long userId, Long chattingRoomId, String contents){
        return new ElasticsearchMessage(userId, chattingRoomId, contents);
    }
}
