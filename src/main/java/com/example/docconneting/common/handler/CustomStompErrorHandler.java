package com.example.docconneting.common.handler;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.common.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import java.nio.charset.StandardCharsets;

@Component
public class CustomStompErrorHandler extends StompSubProtocolErrorHandler {
    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {

        ObjectMapper objectMapper = new ObjectMapper();

        // WebSocket STOMP 내부에서 예외를 MessageDeliveryException 같은 예외로 감싸버림
        ErrorCode errorCode = null;
        if(ex.getCause() instanceof ClientException clientException){
            errorCode = clientException.getErrorCode();
        }

        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        accessor.setContentType(MimeTypeUtils.APPLICATION_JSON);
        // STOMP 헤더중 message message 필드의 값을 설정
        accessor.setMessage(errorCode.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(errorCode);

        try{
            byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);
            // STOMP 의 메지시 본문 body의 값과 헤더의 값 설정 -> 헤더의 message 필드로 간단한 에러 제목, body에는 자세한 내용
            return MessageBuilder.createMessage(bytes, accessor.getMessageHeaders());
        }catch (Exception e){
            String fallback = "{\"name\":\"INTERNAL_SERVER_ERROR\",\"code\":500,\"message\":\"서버 에러\"}";
            return MessageBuilder.createMessage(fallback.getBytes(), accessor.getMessageHeaders());
        }

    }
}
