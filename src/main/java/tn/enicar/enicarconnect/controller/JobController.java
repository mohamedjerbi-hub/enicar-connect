package tn.enicar.enicarconnect.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.enicar.enicarconnect.dto.JobDTO;
import tn.enicar.enicarconnect.model.JobOffer;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.repository.UserRepository;
import tn.enicar.enicarconnect.service.JobService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class JobController {

    private final JobService jobService;
    private final UserRepository userRepo;

    @GetMapping
    public ResponseEntity<List<JobDTO>> getAllJobs(Authentication auth) {
        return ResponseEntity.ok(jobService.getAllJobs(resolveId(auth)));
    }

    @PostMapping
    public ResponseEntity<JobDTO> createJob(@RequestBody JobRequestData req, Authentication auth) {
        JobOffer jobData = JobOffer.builder()
                .title(req.getTitle())
                .company(req.getCompany())
                .location(req.getLocation())
                .type(req.getType())
                .description(req.getDescription())
                .tags(req.getTags() != null ? String.join(",", req.getTags()) : "")
                .build();
        return ResponseEntity.ok(jobService.createJob(jobData, resolveId(auth)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobDTO> updateJob(
            @PathVariable Long id,
            @RequestBody JobRequestData req,
            Authentication auth) {
        JobOffer updatedData = JobOffer.builder()
                .title(req.getTitle())
                .company(req.getCompany())
                .location(req.getLocation())
                .type(req.getType())
                .description(req.getDescription())
                .tags(req.getTags() != null ? String.join(",", req.getTags()) : "")
                .build();
        return ResponseEntity.ok(jobService.updateJob(id, updatedData, resolveId(auth)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id, Authentication auth) {
        jobService.deleteJob(id, resolveId(auth));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/apply")
    public ResponseEntity<Map<String, String>> applyToJob(@PathVariable Long id, Authentication auth) {
        jobService.applyToJob(id, resolveId(auth));
        return ResponseEntity.ok(Map.of("message", "Application submitted successfully"));
    }

    /** Resolve the authenticated user ID from the security context email. O(1) DB index lookup. */
    private Long resolveId(Authentication auth) {
        return userRepo.findByEmail(auth.getName())
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }

    @lombok.Data
    static class JobRequestData {
        private String title;
        private String company;
        private String location;
        private String type;
        private String description;
        private List<String> tags;
    }
}
