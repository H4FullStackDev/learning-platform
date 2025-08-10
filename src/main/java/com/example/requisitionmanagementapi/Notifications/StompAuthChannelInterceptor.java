package com.example.requisitionmanagementapi.Notifications;

import com.example.requisitionmanagementapi.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {
    private final JwtUtil jwtService; // ton service existant

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        var acc = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (acc != null && StompCommand.CONNECT.equals(acc.getCommand())) {
            String auth = acc.getFirstNativeHeader("Authorization");
            if (auth != null && auth.startsWith("Bearer ")) {
                var token = auth.substring(7);
                Authentication authObj = jwtService.toAuthentication(token); // construit un Authentication
                SecurityContextHolder.getContext().setAuthentication(authObj);
                acc.setUser(authObj); // ← définit le Principal pour /user/queue/**
            }
        }
        return message;
    }
}

