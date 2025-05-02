package com.example.docconneting.common.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${server.id}")
    private String id;

    @Value("${chat.queue}")
    private String queue;

    //누락 대비 기본값 설정
    @Value("${chat.elasticsearch:elasticsearch}")
    private String elasticsearchQueue;

    @Value("${chat.exchange}")
    private String exchange;

    @Value("${alarm.queue.name}")
    private String alarmQueueName;

    @Bean
    public Queue queue(){
        return new Queue(queue + "." + id);
    }

    @Bean
    public Queue elasticsearchQueue(){
        return new Queue(elasticsearchQueue);
    }

    @Bean
    public Queue registerAlarmQueue() {
        return new Queue(alarmQueueName, true);
    }

    @Bean
    public FanoutExchange exchange(){
        return new FanoutExchange(exchange);
    }

    @Bean
    public Binding binding(){
        return BindingBuilder
                .bind(queue())
                .to(exchange());
    }

    @Bean
    public Binding elasticsearchBinding(){
        return BindingBuilder
                .bind(elasticsearchQueue())
                .to(exchange());
    }

    @Bean
    public MessageConverter converter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }
}
