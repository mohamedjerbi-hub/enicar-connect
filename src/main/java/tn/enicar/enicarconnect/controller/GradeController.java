package tn.enicar.enicarconnect.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.enicar.enicarconnect.model.Grade;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.repository.UserRepository;
import tn.enicar.enicarconnect.service.GradeService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
public class GradeController {

    private final GradeService gradeService;
    private final UserRepository userRepository;

    @GetMapping("/my-grades")
    public ResponseEntity<List<Grade>> getMyGrades(Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(gradeService.getGradesForStudent(userId));
    }

    @PostMapping("/reclamation")
    public ResponseEntity<Map<String, String>> submitReclamation(
            @RequestBody Map<String, String> body, Authentication auth) {
        Long userId = getUserId(auth);
        Long gradeId = Long.valueOf(body.get("gradeId"));
        String reason = body.get("reason");
        gradeService.submitGradeReclamation(userId, gradeId, reason);
        return ResponseEntity.ok(Map.of("message", "Réclamation envoyée"));
    }

    private Long getUserId(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        return user.getId();
    }
}
