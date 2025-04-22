package com.example.docconneting.domain.chatting.consumer;

import com.example.docconneting.domain.chatting.dto.response.MessageQueuePayload;
import com.example.docconneting.domain.chatting.dto.response.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitMQConsumerService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @RabbitListener(queues = "#{queue.name}")
    public void consume(MessageQueuePayload messageQueuePayload){

        MessageResponse messageResponse = MessageResponse.of(messageQueuePayload.getUserId(), messageQueuePayload.getContents(), messageQueuePayload.getCreatedAt());

        simpMessagingTemplate.convertAndSend("/sub/chattingRooms/" + messageQueuePayload.getChattingRoomId(), messageResponse);
    }

}
