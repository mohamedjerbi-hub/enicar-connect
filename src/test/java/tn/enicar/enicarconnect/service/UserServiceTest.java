package tn.enicar.enicarconnect.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.enicar.enicarconnect.dto.UserDTO;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
    void shouldFindConfiguredUserById() {
        // Arrange
        User mockUser = User.builder().id(1L).email("service@test.com").build();
        UserDTO mockDto = UserDTO.builder().id(1L).email("service@test.com").build();
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(authService.toDTO(any())).thenReturn(mockDto);

        // Act
        UserDTO result = userService.findById(1L);

        // Assert
        assertThat(result.getEmail()).isEqualTo("service@test.com");
        verify(userRepository).findById(1L);
    }
}