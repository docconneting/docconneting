package com.example.docconneting.domain.chatting.consumer;

import com.example.docconneting.domain.chatting.dto.response.MessageQueuePayload;
import com.example.docconneting.domain.chatting.dto.response.MessageResponse;
import com.example.docconneting.domain.chatting.service.ElasticsearchMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitMQConsumerService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ElasticsearchMessageService elasticsearchMessageService;

    @RabbitListener(queues = "#{queue.name}")
    public void consume(MessageQueuePayload messageQueuePayload){

        MessageResponse messageResponse = MessageResponse.of(messageQueuePayload.getUserId(), messageQueuePayload.getContents(), messageQueuePayload.getCreatedAt());

        simpMessagingTemplate.convertAndSend("/sub/chattingRooms/" + messageQueuePayload.getChattingRoomId(), messageResponse);
    }

    @RabbitListener(queues = "${chat.elasticsearch}", concurrency = "3-5")
    public void consumeElasticsearchMessage(MessageQueuePayload messageQueuePayload){
        elasticsearchMessageService.saveMessage(messageQueuePayload.getUserId(), messageQueuePayload.getChattingRoomId(), messageQueuePayload.getContents());
    }

}
