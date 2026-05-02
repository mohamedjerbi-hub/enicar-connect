package tn.enicar.enicarconnect.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.enicar.enicarconnect.dto.UserDTO;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.repository.UserRepository;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthService authService;

    @Transactional(readOnly = true)
    public List<UserDTO> findAll() {
        return userRepository.findAll().stream()
                .map(authService::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        return authService.toDTO(user);
    }

    @Transactional
    public UserDTO update(Long id, UserDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        if (dto.getFirstName() != null)
            user.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null)
            user.setLastName(dto.getLastName());
        if (dto.getPhone() != null)
            user.setPhone(dto.getPhone());
        if (dto.getBio() != null)
            user.setBio(dto.getBio());
        if (dto.getWebsite() != null)
            user.setWebsite(dto.getWebsite());
        if (dto.getLinkedin() != null)
            user.setLinkedin(dto.getLinkedin());
        if (dto.getGithub() != null)
            user.setGithub(dto.getGithub());
        if (dto.getDepartment() != null)
            user.setDepartment(dto.getDepartment());
        if (dto.getLevel() != null)
            user.setLevel(dto.getLevel());
        if (dto.getSkills() != null) {
            Set<String> normalizedSkills = dto.getSkills().stream()
                    .filter(skill -> skill != null && !skill.isBlank())
                    .map(String::trim)
                    .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
            user.setSkills(normalizedSkills);
        }

        userRepository.save(user);
        log.info("User profile updated: userId={} fieldsUpdated={}", id, buildUpdatedFields(dto));
        return authService.toDTO(user);
    }

    private String buildUpdatedFields(UserDTO dto) {
        StringBuilder builder = new StringBuilder();
        if (dto.getFirstName() != null) builder.append("firstName,");
        if (dto.getLastName() != null) builder.append("lastName,");
        if (dto.getPhone() != null) builder.append("phone,");
        if (dto.getBio() != null) builder.append("bio,");
        if (dto.getWebsite() != null) builder.append("website,");
        if (dto.getLinkedin() != null) builder.append("linkedin,");
        if (dto.getGithub() != null) builder.append("github,");
        if (dto.getDepartment() != null) builder.append("department,");
        if (dto.getLevel() != null) builder.append("level,");
        if (dto.getSkills() != null) builder.append("skills,");
        return builder.length() == 0 ? "none" : builder.substring(0, builder.length() - 1);
    }
}
