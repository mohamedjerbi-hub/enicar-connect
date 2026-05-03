package tn.enicar.enicarconnect.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.enicar.enicarconnect.model.DocumentRequest;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.repository.UserRepository;
import tn.enicar.enicarconnect.service.DocumentService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final UserRepository userRepository;

    @PostMapping("/request")
    public ResponseEntity<DocumentRequest> createRequest(@RequestBody Map<String, String> body, Authentication auth) {
        Long userId = getUserId(auth);
        DocumentRequest.RequestType type = DocumentRequest.RequestType.valueOf(body.get("requestType"));
        String notes = body.get("notes");
        return ResponseEntity.ok(documentService.createRequest(userId, type, notes));
    }

    @GetMapping("/my-requests")
    public ResponseEntity<List<DocumentRequest>> getMyRequests(Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(documentService.getMyRequests(userId));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN_STAFF', 'DIRECTION')")
    public ResponseEntity<DocumentRequest> approveRequest(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.approveRequest(id));
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN_STAFF', 'DIRECTION')")
    public ResponseEntity<DocumentRequest> rejectRequest(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(documentService.rejectRequest(id, body.get("reason")));
    }

    private Long getUserId(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        return user.getId();
    }
}
