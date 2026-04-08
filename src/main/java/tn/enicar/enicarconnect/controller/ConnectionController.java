package tn.enicar.enicarconnect.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tn.enicar.enicarconnect.dto.UserDTO;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.service.ConnectionService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/network")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ConnectionController {

    private final ConnectionService connectionService;

    @PostMapping("/request/{receiverId}")
    public ResponseEntity<Map<String, String>> sendRequest(
            @PathVariable Long receiverId,
            @AuthenticationPrincipal User currentUser) {
        connectionService.sendRequest(currentUser.getId(), receiverId);
        return ResponseEntity.ok(Map.of("message", "Demande envoyée avec succès"));
    }

    @PostMapping("/accept/{requestId}")
    public ResponseEntity<Map<String, String>> acceptRequest(
            @PathVariable Long requestId,
            @AuthenticationPrincipal User currentUser) {
        connectionService.acceptRequest(requestId, currentUser.getId());
        return ResponseEntity.ok(Map.of("message", "Connexion acceptée"));
    }

    @PostMapping("/reject/{requestId}")
    public ResponseEntity<Map<String, String>> rejectRequest(
            @PathVariable Long requestId,
            @AuthenticationPrincipal User currentUser) {
        connectionService.rejectRequest(requestId, currentUser.getId());
        return ResponseEntity.ok(Map.of("message", "Connexion refusée"));
    }

    @GetMapping("/connections")
    public ResponseEntity<List<UserDTO>> getMyNetwork(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(connectionService.getMyNetwork(currentUser.getId()));
    }

    @GetMapping("/requests")
    public ResponseEntity<?> getPendingRequests(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(connectionService.getPendingRequests(currentUser.getId()));
    }
}
