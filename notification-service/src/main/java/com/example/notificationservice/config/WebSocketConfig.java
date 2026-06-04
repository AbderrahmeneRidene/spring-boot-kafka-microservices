package com.example.notificationservice.config;

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
        // Le préfixe pour les routes sur lesquelles le serveur envoie des données
        config.enableSimpleBroker("/topic");
        // Le préfixe pour les messages envoyés depuis le frontend vers le backend (optionnel ici)
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Point d'entrée du WebSocket. Le frontend s'y connectera (ex: ws://localhost:8082/ws-notifications)
        registry.addEndpoint("/ws-notifications")
                .setAllowedOrigins("http://localhost:4200"); // Autorise Angular
    }
}
