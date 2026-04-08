package tn.enicar.enicarconnect.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.enicar.enicarconnect.dto.AppEventDTO;
import tn.enicar.enicarconnect.model.AppEvent;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.repository.UserRepository;
import tn.enicar.enicarconnect.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EventController {

    private final EventService eventService;
    private final UserRepository userRepo;

    @GetMapping
    public ResponseEntity<List<AppEventDTO>> getAllEvents(Authentication auth) {
        return ResponseEntity.ok(eventService.getAllEvents(resolveId(auth)));
    }

    @PostMapping
    public ResponseEntity<AppEventDTO> createEvent(@RequestBody AppEvent eventData, Authentication auth) {
        return ResponseEntity.ok(eventService.createEvent(eventData, resolveId(auth)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppEventDTO> updateEvent(
            @PathVariable Long id,
            @RequestBody AppEvent eventData,
            Authentication auth) {
        return ResponseEntity.ok(eventService.updateEvent(id, eventData, resolveId(auth)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id, Authentication auth) {
        eventService.deleteEvent(id, resolveId(auth));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/toggle-register")
    public ResponseEntity<AppEventDTO> toggleRegister(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(eventService.toggleRegister(id, resolveId(auth)));
    }

    /** Resolve the authenticated user ID from the security context email. O(1) DB index lookup. */
    private Long resolveId(Authentication auth) {
        return userRepo.findByEmail(auth.getName())
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }
}
