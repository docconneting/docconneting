package com.example.docconneting.common.config;

import com.example.docconneting.common.exception.object.ClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import org.springframework.util.ErrorHandler;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;

@Configuration
@Slf4j
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

    @Value("${coupon.queue.coupon-name}")
    private String couponQueueName;

    @Value("${coupon.queue.dlq-name}")
    private String dlqQueueName;

    @Value("${coupon.queue.retry-name}")
    private String retryQueueName;

    @Value("${coupon.queue.fail-name}")
    private String failQueueName;

    @Value("${coupon.exchange.name}")
    private String exchangeName;

    @Value("${coupon.routing.key}")
    private String routingKey;

    @Value("${coupon.routing.dlq-key}")
    private String dlqRoutingKey;

    @Value("${coupon.routing.retry-key}")
    private String retryRoutingKey;

    @Value("${coupon.routing.fail-key}")
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
    public DirectExchange couponExchange() {
        return new DirectExchange(exchangeName);
    }

    @Bean
    public Binding couponBinding() {
        return BindingBuilder.bind(couponQueue()).to(couponExchange()).with(routingKey);
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

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setErrorHandler(customErrorHandler());
        factory.setMessageConverter(converter());
        return factory;
    }

    @Bean
    public ErrorHandler customErrorHandler() {
        return new ConditionalRejectingErrorHandler(new ConditionalRejectingErrorHandler.DefaultExceptionStrategy() {
            @Override
            public boolean isFatal(Throwable t) {
                Throwable cause = t.getCause();
                if (cause instanceof ClientException) {
                    log.warn("비즈니스 예외 발생. 메시지 버림: {}", cause.getMessage());
                    return false; // ClientException는 → ACK 처리됨
                }
                return true; // 그 외는 치명적 → 재시도 또는 DLQ
            }
        });
    }
}
