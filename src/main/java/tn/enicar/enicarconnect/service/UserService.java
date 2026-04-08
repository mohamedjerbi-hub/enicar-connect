package tn.enicar.enicarconnect.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.enicar.enicarconnect.dto.UserDTO;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthService authService;

    public List<UserDTO> findAll() {
        return userRepository.findAll().stream()
                .map(authService::toDTO)
                .toList();
    }

    public UserDTO findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        return authService.toDTO(user);
    }

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

        userRepository.save(user);
        return authService.toDTO(user);
    }
}
