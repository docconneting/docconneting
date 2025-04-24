package com.example.docconneting.common.interceptor;

import com.example.docconneting.common.config.JwtUtil;
import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.common.principal.StompPrincipal;
import com.example.docconneting.domain.user.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;



    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor stompHeaderAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(stompHeaderAccessor.getCommand())) {

            String bearerToken = stompHeaderAccessor.getFirstNativeHeader("Authorization");

            if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
                throw new ClientException(ErrorCode.JWT_TOKEN_REQUIRED);
            }

            String jwt = jwtUtil.substringToken(bearerToken);
            try {
                Claims claims = jwtUtil.extractClaims(jwt);
                if (claims == null) {
                    throw new ClientException(ErrorCode.INVALID_JWT_FORMAT);
                }

                Long userId = Long.parseLong(claims.getSubject());
                String role = claims.get("role", String.class);

                Principal principal = new StompPrincipal(userId.toString());
                stompHeaderAccessor.setUser(principal);

            } catch (SecurityException | MalformedJwtException e) {
                throw new ClientException(ErrorCode.INVALID_JWT_SIGNATURE);
            } catch (ExpiredJwtException e) {
                throw new ClientException(ErrorCode.EXPIRED_JWT_TOKEN);
            } catch (UnsupportedJwtException e) {
                throw new ClientException(ErrorCode.UNSUPPORTED_JWT_TOKEN);
            } catch (Exception e) {
                throw new ClientException(ErrorCode.INVALID_JWT_TOKEN);
            }
        }

        return message;
    }
}
