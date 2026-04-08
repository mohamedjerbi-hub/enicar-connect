package tn.enicar.enicarconnect.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tn.enicar.enicarconnect.model.ChatMessage;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.repository.ChatMessageRepository;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;

    /**
     * Reçoit les messages via WebSocket sur la route /app/chat
     * Le message envoyé par le frontend doit être un JSON au format :
     * { "senderId": 1, "recipientId": 2, "content": "Bonjour" }
     */
    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage) {
        // Le timestamp est set automatiquement par @CreationTimestamp au moment de la
        // sauvegarde
        // Mais pour informer le Front-End en vrai Temps-Réel, on génère une date
        // temporaire si besoin
        chatMessage.setTimestamp(LocalDateTime.now());
        chatMessage.setRead(false);

        // Sauvegarde de l'historique
        ChatMessage savedMsg = chatMessageRepository.save(chatMessage);

        // Envoi au destinataire via son canal privé (abonné à
        // /user/{id}/queue/messages)
        messagingTemplate.convertAndSendToUser(
                String.valueOf(chatMessage.getRecipientId()),
                "/queue/messages",
                savedMsg);
    }

    /**
     * Route API classique pour charger l'historique quand on clique sur une
     * conversation.
     */
    @GetMapping("/api/messages/{recipientId}")
    public ResponseEntity<List<ChatMessage>> getConversation(
            @PathVariable Long recipientId,
            @AuthenticationPrincipal User currentUser) {
        List<ChatMessage> messages = chatMessageRepository.findConversation(currentUser.getId(), recipientId);
        return ResponseEntity.ok(messages);
    }
}
