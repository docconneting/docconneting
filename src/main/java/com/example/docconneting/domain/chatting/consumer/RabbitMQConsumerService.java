package com.example.docconneting.domain.chatting.consumer;

import com.example.docconneting.domain.chatting.dto.response.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitMQConsumerService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @RabbitListener(queues = "#{queue.name}")
    public void consume(MessageResponse messageResponse){
        simpMessagingTemplate.convertAndSend("/sub/chattingRooms/" + messageResponse.getChattingRoomId(), messageResponse);
    }

}
