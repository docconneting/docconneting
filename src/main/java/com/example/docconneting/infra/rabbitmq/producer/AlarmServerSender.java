package com.example.docconneting.infra.rabbitmq.producer;

import com.example.docconneting.infra.rabbitmq.dto.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlarmServerSender {

    private final RabbitTemplate rabbitTemplate;

    @Value("${alarm.queue.name}")
    private String queue;

    public void send(Message message) {
        rabbitTemplate.convertAndSend(queue, message);
    }

}
