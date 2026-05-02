package tn.enicar.enicarconnect.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.enicar.enicarconnect.dto.UserDTO;
import tn.enicar.enicarconnect.model.Role;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.repository.UserRepository;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private UserService userService;

    @Test
    void update_shouldNormalizeSkillsAndPersist() {
        User user = User.builder()
                .id(1L)
                .email("user@enicar.tn")
                .firstName("A")
                .lastName("B")
                .role(Role.STUDENT)
                .skills(Set.of("java"))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDTO dto = UserDTO.builder()
                .skills(Set.of("  Java  ", "", "Spring Boot", "Java"))
                .build();

        UserDTO mapped = UserDTO.builder()
                .id(1L)
                .skills(Set.of("Java", "Spring Boot"))
                .build();

        when(authService.toDTO(user)).thenReturn(mapped);

        UserDTO result = userService.update(1L, dto);

        assertEquals(Set.of("Java", "Spring Boot"), user.getSkills());
        assertEquals(Set.of("Java", "Spring Boot"), result.getSkills());
        verify(userRepository).save(user);
    }
}

