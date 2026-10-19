package tn.enicar.enicarconnect.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.enicar.enicarconnect.dto.UserDTO;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests (Senior QA)")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("TC-USER-01: Should find user by ID when present")
    void shouldFindConfiguredUserById() {
        // Arrange
        User mockUser = User.builder().id(1L).email("service@test.com").build();
        UserDTO mockDto = UserDTO.builder().id(1L).email("service@test.com").build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(authService.toDTO(any())).thenReturn(mockDto);

        // Act
        UserDTO result = userService.findById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("service@test.com");
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("TC-USER-02: Should throw RuntimeException when user not found by ID")
    void shouldThrowWhenUserNotFoundById() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.findById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Utilisateur introuvable");
        verify(userRepository).findById(999L);
    }

    @Test
    @DisplayName("TC-USER-03: Should return all users converted to DTO")
    void shouldFindAllUsers() {
        // Arrange
        User u1 = User.builder().id(1L).email("user1@enicar.tn").build();
        User u2 = User.builder().id(2L).email("user2@enicar.tn").build();
        UserDTO d1 = UserDTO.builder().id(1L).email("user1@enicar.tn").build();
        UserDTO d2 = UserDTO.builder().id(2L).email("user2@enicar.tn").build();

        when(userRepository.findAll()).thenReturn(List.of(u1, u2));
        when(authService.toDTO(u1)).thenReturn(d1);
        when(authService.toDTO(u2)).thenReturn(d2);

        // Act
        List<UserDTO> results = userService.findAll();

        // Assert
        assertThat(results).hasSize(2);
        assertThat(results).extracting(UserDTO::getEmail).containsExactly("user1@enicar.tn", "user2@enicar.tn");
    }

    @Test
    @DisplayName("TC-USER-04: Should update user profile fields and normalize skills")
    void shouldUpdateUserProfileAndNormalizeSkills() {
        // Arrange
        User existingUser = User.builder()
                .id(1L)
                .email("student@enicar.tn")
                .firstName("OldFirst")
                .lastName("OldLast")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserDTO updateDto = UserDTO.builder()
                .firstName("NewFirst")
                .lastName("NewLast")
                .bio("New Bio")
                .skills(Set.of("  Java  ", "Spring Boot", "", "   "))
                .build();

        UserDTO returnedDto = UserDTO.builder().id(1L).firstName("NewFirst").build();
        when(authService.toDTO(existingUser)).thenReturn(returnedDto);

        // Act
        UserDTO result = userService.update(1L, updateDto);

        // Assert
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();

        assertThat(saved.getFirstName()).isEqualTo("NewFirst");
        assertThat(saved.getLastName()).isEqualTo("NewLast");
        assertThat(saved.getBio()).isEqualTo("New Bio");
        assertThat(saved.getSkills()).containsExactlyInAnyOrder("Java", "Spring Boot");
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("TC-USER-05: Should throw RuntimeException when updating non-existent user")
    void shouldThrowWhenUpdatingNonExistentUser() {
        // Arrange
        when(userRepository.findById(404L)).thenReturn(Optional.empty());
        UserDTO updateDto = UserDTO.builder().firstName("Test").build();

        // Act & Assert
        assertThatThrownBy(() -> userService.update(404L, updateDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Utilisateur introuvable");
    }
}