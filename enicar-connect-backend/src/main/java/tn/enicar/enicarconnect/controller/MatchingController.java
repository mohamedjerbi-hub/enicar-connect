package tn.enicar.enicarconnect.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.enicar.enicarconnect.dto.MatchingResultDTO;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.repository.UserRepository;
import tn.enicar.enicarconnect.service.MatchingService;

import java.util.List;

@RestController
@RequestMapping("/api/matching")
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingService matchingService;
    private final UserRepository userRepository;

    @GetMapping("/jobs")
    @PreAuthorize("hasAnyRole('STUDENT', 'ALUMNI', 'TEACHER', 'ADMIN_STAFF', 'DIRECTION')")
    public ResponseEntity<List<MatchingResultDTO>> getJobMatchesForCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(matchingService.getMatchingResultsForUser(currentUser.getId()));
    }
}
