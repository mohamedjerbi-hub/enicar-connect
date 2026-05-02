package tn.enicar.enicarconnect.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tn.enicar.enicarconnect.dto.JobDTO;
import tn.enicar.enicarconnect.model.JobOffer;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.service.JobService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class JobController {

    private final JobService jobService;

    @GetMapping
    public ResponseEntity<List<JobDTO>> getAllJobs(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(jobService.getAllJobs(currentUser.getId()));
    }

    @PostMapping
    public ResponseEntity<JobDTO> createJob(
            @RequestBody JobRequestData requestData,
            @AuthenticationPrincipal User currentUser) {
        // Build JobOffer from request data (converting tags List to String)
        String tagsString = requestData.getTags() != null
                ? String.join(",", requestData.getTags())
                : "";

        JobOffer jobData = JobOffer.builder()
                .title(requestData.getTitle())
                .company(requestData.getCompany())
                .location(requestData.getLocation())
                .type(requestData.getType())
                .description(requestData.getDescription())
                .tags(tagsString)
                .requiredSkills(resolveRequiredSkills(requestData))
                .build();

        return ResponseEntity.ok(jobService.createJob(jobData, currentUser.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobDTO> updateJob(
            @PathVariable Long id,
            @RequestBody JobRequestData requestData,
            @AuthenticationPrincipal User currentUser) {
        String tagsString = requestData.getTags() != null
                ? String.join(",", requestData.getTags())
                : "";

        JobOffer updatedData = JobOffer.builder()
                .title(requestData.getTitle())
                .company(requestData.getCompany())
                .location(requestData.getLocation())
                .type(requestData.getType())
                .description(requestData.getDescription())
                .tags(tagsString)
                .requiredSkills(resolveRequiredSkills(requestData))
                .build();

        return ResponseEntity.ok(jobService.updateJob(id, updatedData, currentUser.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        jobService.deleteJob(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/apply")
    public ResponseEntity<Map<String, String>> applyToJob(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        jobService.applyToJob(id, currentUser.getId());
        return ResponseEntity.ok(Map.of("message", "Application submitted successfully"));
    }

    // Helper class to receive data from frontend matching Job interface
    @lombok.Data
    static class JobRequestData {
        private String title;
        private String company;
        private String location;
        private String type;
        private String description;
        private List<String> tags;
        private Set<String> requiredSkills;
    }

    private Set<String> resolveRequiredSkills(JobRequestData requestData) {
        if (requestData.getRequiredSkills() != null && !requestData.getRequiredSkills().isEmpty()) {
            return normalizeSkills(requestData.getRequiredSkills());
        }
        if (requestData.getTags() != null && !requestData.getTags().isEmpty()) {
            return requestData.getTags().stream()
                    .filter(skill -> skill != null && !skill.isBlank())
                    .map(String::trim)
                    .collect(Collectors.toCollection(java.util.LinkedHashSet::new));
        }
        return Set.of();
    }

    private Set<String> normalizeSkills(Set<String> rawSkills) {
        return rawSkills.stream()
                .filter(skill -> skill != null && !skill.isBlank())
                .map(String::trim)
                .collect(Collectors.toCollection(java.util.LinkedHashSet::new));
    }
}
