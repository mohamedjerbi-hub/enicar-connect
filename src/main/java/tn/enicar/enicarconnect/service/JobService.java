package tn.enicar.enicarconnect.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.enicar.enicarconnect.dto.JobDTO;
import tn.enicar.enicarconnect.model.JobApplication;
import tn.enicar.enicarconnect.model.JobOffer;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.repository.JobApplicationRepository;
import tn.enicar.enicarconnect.repository.JobOfferRepository;
import tn.enicar.enicarconnect.repository.UserRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobService {

    private final JobOfferRepository jobOfferRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<JobDTO> getAllJobs(Long currentUserId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        log.info("Loading jobs for userId={}", currentUserId);
        return jobOfferRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(job -> mapToDTO(job, currentUser))
                .collect(Collectors.toList());
    }

    @Transactional
    public JobDTO createJob(JobOffer jobData, Long currentUserId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        jobData.setAuthor(currentUser);
        JobOffer saved = jobOfferRepository.save(jobData);
        log.info("Job created: jobId={} authorId={}", saved.getId(), currentUserId);
        return mapToDTO(saved, currentUser);
    }

    @Transactional
    public JobDTO updateJob(Long id, JobOffer updatedData, Long currentUserId) {
        JobOffer job = jobOfferRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job offer not found"));
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!job.getAuthor().getId().equals(currentUserId)) {
            throw new RuntimeException("Unauthorized");
        }

        job.setTitle(updatedData.getTitle());
        job.setCompany(updatedData.getCompany());
        job.setLocation(updatedData.getLocation());
        job.setType(updatedData.getType());
        job.setDescription(updatedData.getDescription());
        job.setTags(updatedData.getTags());
        if (updatedData.getRequiredSkills() != null) {
            job.getRequiredSkills().clear();
            job.getRequiredSkills().addAll(updatedData.getRequiredSkills());
        }

        JobOffer saved = jobOfferRepository.save(job);
        log.info("Job updated: jobId={} userId={}", id, currentUserId);
        return mapToDTO(saved, currentUser);
    }

    @Transactional
    public void deleteJob(Long id, Long currentUserId) {
        JobOffer job = jobOfferRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job offer not found"));

        if (!job.getAuthor().getId().equals(currentUserId)) {
            throw new RuntimeException("Unauthorized");
        }

        jobOfferRepository.delete(job);
        log.info("Job deleted: jobId={} userId={}", id, currentUserId);
    }

    @Transactional
    public void applyToJob(Long jobId, Long currentUserId) {
        JobOffer job = jobOfferRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job offer not found"));
        User applicant = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Vérifier si le candidat est le créateur (optionnel, mais logique)
        if (job.getAuthor().getId().equals(currentUserId)) {
            throw new RuntimeException("Vous ne pouvez pas candidater à votre propre offre");
        }

        // Vérifier s'il a déjà postulé
        if (jobApplicationRepository.existsByJobOfferAndApplicant(job, applicant)) {
            return; // Déjà postulé, silence
        }

        JobApplication application = JobApplication.builder()
                .jobOffer(job)
                .applicant(applicant)
                .status("PENDING")
                .build();
        jobApplicationRepository.save(application);
        log.info("Job application created: jobId={} applicantId={}", jobId, currentUserId);
    }

    private JobDTO mapToDTO(JobOffer job, User currentUser) {
        boolean isOwner = job.getAuthor().getId().equals(currentUser.getId());
        boolean isApplied = jobApplicationRepository.existsByJobOfferAndApplicant(job, currentUser);

        List<String> tagsList = job.getTags() == null || job.getTags().isEmpty()
                ? List.of()
                : Arrays.asList(job.getTags().split(","));

        return JobDTO.builder()
                .id(job.getId())
                .title(job.getTitle())
                .company(job.getCompany())
                .location(job.getLocation())
                .type(job.getType())
                .description(job.getDescription())
                .tags(tagsList)
                .requiredSkills(job.getRequiredSkills())
                .posted(getRelativeTime(job.getCreatedAt()))
                .applied(isApplied)
                .isOwner(isOwner)
                .build();
    }

    private String getRelativeTime(LocalDateTime createdAt) {
        if (createdAt == null)
            return "À l'instant";

        Duration duration = Duration.between(createdAt, LocalDateTime.now());
        long diffHours = duration.toHours();
        long diffDays = duration.toDays();

        if (diffHours < 1)
            return "Il y a moins d'une heure";
        if (diffHours < 24)
            return "Il y a " + diffHours + " heure(s)";
        if (diffDays == 1)
            return "Il y a " + diffDays + " jour";
        if (diffDays < 7)
            return "Il y a " + diffDays + " jours";
        long weeks = diffDays / 7;
        return "Il y a " + weeks + " semaine(s)";
    }
}
