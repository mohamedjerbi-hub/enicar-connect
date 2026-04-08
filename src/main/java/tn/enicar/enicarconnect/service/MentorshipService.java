package tn.enicar.enicarconnect.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.enicar.enicarconnect.dto.MentorshipDTO;
import tn.enicar.enicarconnect.dto.UserDTO;
import tn.enicar.enicarconnect.model.MentorshipRequest;
import tn.enicar.enicarconnect.model.Role;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.repository.MentorshipRequestRepository;
import tn.enicar.enicarconnect.repository.UserRepository;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MentorshipService {

    private final MentorshipRequestRepository mentorshipRepo;
    private final UserRepository userRepository;

    // Lister les mentors disponibles
    public List<UserDTO> getAvailableMentors() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.ALUMNI || u.getRole() == Role.TEACHER)
                .map(u -> UserDTO.builder()
                        .id(u.getId())
                        .firstName(u.getFirstName())
                        .lastName(u.getLastName())
                        .email(u.getEmail())
                        .role(u.getRole().name())
                        .department(u.getDepartment())
                        .build())
                .collect(Collectors.toList());
    }

    // Postuler (Demande d'un étudiant à un ALUMNI/TEACHER)
    public MentorshipDTO requestMentorship(Long mentorId, String objective, Long currentUserId) {
        User mentee = userRepository.findById(currentUserId).orElseThrow();
        User mentor = userRepository.findById(mentorId).orElseThrow();

        if (mentee.getId().equals(mentor.getId())) {
            throw new RuntimeException("Vous ne pouvez pas être votre propre mentor.");
        }

        if (mentorshipRepo.existsByMentorAndMenteeAndStatusIn(mentor, mentee, Arrays.asList("PENDING", "ACTIVE"))) {
            throw new RuntimeException("Demande existante ou déjà active vers ce mentor.");
        }

        MentorshipRequest request = MentorshipRequest.builder()
                .mentee(mentee)
                .mentor(mentor)
                .objective(objective)
                .status("PENDING")
                .build();

        MentorshipRequest saved = mentorshipRepo.save(request);
        return mapToDTO(saved, currentUserId);
    }

    // Mentor accepte la demande
    public void acceptRequest(Long requestId, Long currentUserId) {
        MentorshipRequest request = mentorshipRepo.findById(requestId).orElseThrow();
        if (!request.getMentor().getId().equals(currentUserId)) {
            throw new RuntimeException("Permission refusée.");
        }
        request.setStatus("ACTIVE");
        mentorshipRepo.save(request);
    }

    // Mentor refuse la demande
    public void rejectRequest(Long requestId, Long currentUserId) {
        MentorshipRequest request = mentorshipRepo.findById(requestId).orElseThrow();
        if (!request.getMentor().getId().equals(currentUserId)) {
            throw new RuntimeException("Permission refusée.");
        }
        request.setStatus("REJECTED");
        mentorshipRepo.save(request);
    }

    // Mentor clôture le programme
    public void completeMentorship(Long requestId, Long currentUserId) {
        MentorshipRequest request = mentorshipRepo.findById(requestId).orElseThrow();
        if (!request.getMentor().getId().equals(currentUserId) && !request.getMentee().getId().equals(currentUserId)) {
            throw new RuntimeException("Permission refusée.");
        }
        request.setStatus("COMPLETED");
        mentorshipRepo.save(request);
    }

    // Obtenir le tableau de bord du mentee
    public List<MentorshipDTO> getMyMentorshipsAsMentee(Long currentUserId) {
        User mentee = userRepository.findById(currentUserId).orElseThrow();
        return mentorshipRepo.findByMenteeOrderByCreatedAtDesc(mentee).stream()
                .map(r -> mapToDTO(r, currentUserId))
                .collect(Collectors.toList());
    }

    // Obtenir le tableau de bord du mentor
    public List<MentorshipDTO> getMyMentorshipsAsMentor(Long currentUserId) {
        User mentor = userRepository.findById(currentUserId).orElseThrow();
        return mentorshipRepo.findByMentorOrderByCreatedAtDesc(mentor).stream()
                .map(r -> mapToDTO(r, currentUserId))
                .collect(Collectors.toList());
    }

    private MentorshipDTO mapToDTO(MentorshipRequest r, Long currentUserId) {
        boolean iAmMentee = r.getMentee().getId().equals(currentUserId);
        User partner = iAmMentee ? r.getMentor() : r.getMentee();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return MentorshipDTO.builder()
                .id(r.getId())
                .partnerId(partner.getId())
                .partnerName(partner.getFullName())
                .partnerRole(partner.getRole().name())
                .partnerDepartment(partner.getDepartment() != null ? partner.getDepartment() : "ENICAR")
                .objective(r.getObjective())
                .status(r.getStatus())
                .date(r.getCreatedAt() != null ? r.getCreatedAt().format(formatter) : "")
                .build();
    }
}
