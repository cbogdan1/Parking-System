package com.parcare.parking_system.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Activeaza un broker in-memory pe prefixul "/topic" (pentru mesaje server->client)
        config.enableSimpleBroker("/topic");
        // Prefixul pentru mesajele pe care le trimite clientul catre server (client->server)
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Inregistram endpoint-ul "/ws" pe care se va conecta aplicatia React
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // Permitem conexiuni de pe orice frontend (React)
                .withSockJS(); // Fallback la SockJS daca WebSockets nu sunt suportate de browser
    }
}
