package tn.enicar.enicarconnect.config;

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
        // Paramétrage du broker STOMP
        config.enableSimpleBroker("/user", "/topic"); // "/user" pour les messages privés (P2P), "/topic" pour groupes
        config.setApplicationDestinationPrefixes("/app"); // Préfixe pour les messages envoyés au serveur
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Point d'entrée WebSocket (avec Fallback SockJS)
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*"); // Autoriser depuis n'importe où (à restreindre en production)
        // .withSockJS(); // Optionnel: activer si STOMP natif bloque, mais souvent non
        // nécessaire avec @stomp/stompjs
    }
}
