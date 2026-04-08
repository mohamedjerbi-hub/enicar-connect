package tn.enicar.enicarconnect.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tn.enicar.enicarconnect.dto.MentorshipDTO;
import tn.enicar.enicarconnect.dto.UserDTO;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.service.MentorshipService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mentorship")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MentorshipController {

    private final MentorshipService mentorshipService;

    // Lister les mentors
    @GetMapping("/available-mentors")
    public ResponseEntity<List<UserDTO>> getAvailableMentors() {
        return ResponseEntity.ok(mentorshipService.getAvailableMentors());
    }

    // Demander le mentorat
    @PostMapping("/request/{mentorId}")
    public ResponseEntity<MentorshipDTO> requestMentorship(
            @PathVariable Long mentorId,
            @RequestBody Map<String, String> payload,
            @AuthenticationPrincipal User currentUser) {
        String objective = payload.getOrDefault("objective", "Demande de mentorat générale.");
        return ResponseEntity.ok(mentorshipService.requestMentorship(mentorId, objective, currentUser.getId()));
    }

    // Accepter (Mentor only)
    @PostMapping("/accept/{requestId}")
    public ResponseEntity<Void> acceptRequest(
            @PathVariable Long requestId,
            @AuthenticationPrincipal User currentUser) {
        mentorshipService.acceptRequest(requestId, currentUser.getId());
        return ResponseEntity.ok().build();
    }

    // Refuser (Mentor only)
    @PostMapping("/reject/{requestId}")
    public ResponseEntity<Void> rejectRequest(
            @PathVariable Long requestId,
            @AuthenticationPrincipal User currentUser) {
        mentorshipService.rejectRequest(requestId, currentUser.getId());
        return ResponseEntity.ok().build();
    }

    // Fermer
    @PostMapping("/complete/{requestId}")
    public ResponseEntity<Void> completeRequest(
            @PathVariable Long requestId,
            @AuthenticationPrincipal User currentUser) {
        mentorshipService.completeMentorship(requestId, currentUser.getId());
        return ResponseEntity.ok().build();
    }

    // Historique/Dashboard pour un Mentee (Etudiant)
    @GetMapping("/my-requests")
    public ResponseEntity<List<MentorshipDTO>> getAsMentee(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(mentorshipService.getMyMentorshipsAsMentee(currentUser.getId()));
    }

    // Historique/Dashboard pour un Mentor (Alumni/Enseignant)
    @GetMapping("/my-mentees")
    public ResponseEntity<List<MentorshipDTO>> getAsMentor(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(mentorshipService.getMyMentorshipsAsMentor(currentUser.getId()));
    }
}
