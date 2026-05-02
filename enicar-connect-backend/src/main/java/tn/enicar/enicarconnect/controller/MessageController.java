package tn.enicar.enicarconnect.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.enicar.enicarconnect.dto.MessageDTO;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.repository.UserRepository;
import tn.enicar.enicarconnect.service.MessageService;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final UserRepository userRepository;

    @GetMapping("/{groupId}/messages")
    public ResponseEntity<List<MessageDTO>> getGroupMessages(
            @PathVariable Long groupId,
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);
        return ResponseEntity.ok(messageService.getGroupMessages(userId, groupId));
    }

    @PostMapping("/{groupId}/messages")
    public ResponseEntity<MessageDTO> sendGroupMessage(
            @PathVariable Long groupId,
            @RequestBody SendGroupMessageRequest request,
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);
        return ResponseEntity.ok(messageService.sendMessage(userId, groupId, request.getContent()));
    }

    private Long getUserId(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        return user.getId();
    }

    @Data
    public static class SendGroupMessageRequest {
        private String content;
    }
}

