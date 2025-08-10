package com.example.requisitionmanagementapi.Notifications;

import com.example.requisitionmanagementapi.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class NotificationsWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompAuthChannelInterceptor stompAuthInterceptor;

    /**
     * Point d’entrée WebSocket.
     * - "/ws" : URL à laquelle le client se connecte.
     * - withSockJS() : fallback XHR/iframe si WebSocket non dispo.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/ws")
                .setAllowedOriginPatterns("http://localhost:4200","https://admin.ton-domaine.tg","https://*.ton-domaine.tg")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompAuthInterceptor); // ← indispensable pour lire le JWT au CONNECT
    }


    /**
     * Configuration du broker de messages :
     * - enableSimpleBroker("/topic","/queue") : broker en mémoire,
     *   routes tous les messages envoyés sur /topic/** et /queue/** vers les clients.
     * - setApplicationDestinationPrefixes("/app") :
     *   toutes les destinations commençant par /app vont vers les @MessageMapping.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        var simple = registry.enableSimpleBroker("/topic", "/queue");
        simple.setHeartbeatValue(new long[]{10000, 10000});
        simple.setTaskScheduler(websocketTaskScheduler());

        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user"); // ← explicite
    }

    @Bean
    public ThreadPoolTaskScheduler websocketTaskScheduler() {
        var ts = new ThreadPoolTaskScheduler();
        ts.setPoolSize(1);
        ts.setThreadNamePrefix("ws-heartbeat-");
        ts.initialize();
        return ts;
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration r) {
        r.setMessageSizeLimit(64 * 1024)
                .setSendBufferSizeLimit(512 * 1024)
                .setSendTimeLimit(20_000);
    }

}

