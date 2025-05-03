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

    @Value("${chat.elasticsearch}")
    private String elasticsearchQueue;

    @Value("${chat.exchange}")
    private String exchange;

    @Value("${alarm.queue.name}")
    private String alarmQueueName;

    @Value("${rabbitmq.queue.coupon-name}")
    private String couponQueueName;

    @Value("${rabbitmq.queue.dlq-name}")
    private String dlqQueueName;

    @Value("${rabbitmq.queue.retry-name}")
    private String retryQueueName;

    @Value("${rabbitmq.queue.fail-name}")
    private String failQueueName;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    @Value("${rabbitmq.routing.dlq-key}")
    private String dlqRoutingKey;

    @Value("${rabbitmq.routing.retry-key}")
    private String retryRoutingKey;

    @Value("${rabbitmq.routing.fail-key}")
    private String failRoutingKey;

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
    public Queue couponQueue() {
        return QueueBuilder.durable(couponQueueName)
                .withArgument("x-dead-letter-exchange", exchangeName)
                .withArgument("x-dead-letter-routing-key", dlqRoutingKey)
                .build();
    }

    // DLQ (죽은 메세지 큐)
    @Bean
    public Queue dlqQueue() {
        return QueueBuilder.durable(dlqQueueName).build();
    }

    // 재시도 큐
    @Bean
    public Queue retryQueue() {
        return QueueBuilder.durable(retryQueueName).build();
    }

    // 최종 실패 정보 저장 큐
    @Bean
    public Queue failQueue() {
        return QueueBuilder.durable(failQueueName).build();
    }

    @Bean
    public FanoutExchange exchange(){
        return new FanoutExchange(exchange);
    }

    @Bean
    public DirectExchange couponExchange() {
        return new DirectExchange(exchangeName);
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
    public Binding couponBinding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }

    // 쿠폰 DLQ 바인딩
    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(dlqQueue()).to(couponExchange()).with(dlqRoutingKey);
    }

    // 쿠폰 재시도 큐 바인딩
    @Bean
    public Binding retryBinding() {
        return BindingBuilder.bind(retryQueue()).to(couponExchange()).with(retryRoutingKey);
    }

    // 쿠폰 실패 큐 바인딩
    @Bean
    public Binding failBinding() {
        return BindingBuilder.bind(failQueue()).to(couponExchange()).with(failRoutingKey);
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
