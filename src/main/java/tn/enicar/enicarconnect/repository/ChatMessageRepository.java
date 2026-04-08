package tn.enicar.enicarconnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tn.enicar.enicarconnect.model.ChatMessage;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // Récupérer la conversation entre deux utilisateurs (triée de l'ancien au
    // récent)
    @Query("SELECT m FROM ChatMessage m WHERE " +
            "(m.senderId = :user1 AND m.recipientId = :user2) " +
            "OR (m.senderId = :user2 AND m.recipientId = :user1) " +
            "ORDER BY m.timestamp ASC")
    List<ChatMessage> findConversation(Long user1, Long user2);

}
