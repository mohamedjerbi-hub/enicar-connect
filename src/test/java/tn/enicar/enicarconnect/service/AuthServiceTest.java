package tn.enicar.enicarconnect.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import tn.enicar.enicarconnect.config.RolePermissionConfig;
import tn.enicar.enicarconnect.dto.AuthResponse;
import tn.enicar.enicarconnect.dto.LoginRequest;
import tn.enicar.enicarconnect.dto.RegisterRequest;
import tn.enicar.enicarconnect.dto.UserDTO;
import tn.enicar.enicarconnect.model.AppGroup;
import tn.enicar.enicarconnect.model.MemberRole;
import tn.enicar.enicarconnect.model.Role;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests (Senior QA)")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private RolePermissionConfig rolePermissionConfig;

    @Mock
    private GroupService groupService;

    @InjectMocks
    private AuthService authService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        org.mockito.Mockito.lenient().when(rolePermissionConfig.getPermissionNames(any())).thenReturn(java.util.List.of("POST_READ"));
    }

    @Test
    @DisplayName("TC-AUTH-01: Should register student successfully with BCrypt encoding and token generation")
    void shouldRegisterStudentSuccessfully() {
        // Arrange
        RegisterRequest req = new RegisterRequest();
        req.setEmail("babou.lead@enicar.tn");
        req.setPassword("PlainPassword123!");
        req.setFirstName("Mohamed");
        req.setLastName("Babou");
        req.setRole("STUDENT");
        req.setLevel("2ème");
        req.setDepartment("INFO");

        when(userRepository.existsByEmail("babou.lead@enicar.tn")).thenReturn(false);
        when(passwordEncoder.encode("PlainPassword123!")).thenReturn("$2a$10$encodedHash...");
        when(jwtService.generateToken(any(), any())).thenReturn("mocked.jwt.token");
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId(1L);
            return u;
        });
        AppGroup mockGroup = new AppGroup();
        when(groupService.ensureDefaultGroup(any(), any(), any())).thenReturn(mockGroup);

        // Act
        AuthResponse response = authService.register(req);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("mocked.jwt.token");
        assertThat(response.getUser().getEmail()).isEqualTo("babou.lead@enicar.tn");
        verify(userRepository).save(any(User.class));
        verify(groupService).addUserToGroup(eq(mockGroup), any(User.class), eq(MemberRole.MEMBER));
    }

    @Test
    @DisplayName("TC-AUTH-02: Should throw exception when registering with already existing email")
    void shouldRejectRegisterWithDuplicateEmail() {
        // Arrange
        RegisterRequest req = new RegisterRequest();
        req.setEmail("existing@enicar.tn");
        when(userRepository.existsByEmail("existing@enicar.tn")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> authService.register(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Un compte avec cet email existe déjà");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("TC-AUTH-03: Should login successfully and return JWT token")
    void shouldLoginSuccessfully() {
        // Arrange
        LoginRequest req = new LoginRequest();
        req.setEmail("oussema@enicar.tn");
        req.setPassword("SecretPass123");

        User user = User.builder()
                .id(2L)
                .email("oussema@enicar.tn")
                .role(Role.STUDENT)
                .firstName("Oussema")
                .lastName("Mejri")
                .build();

        when(userRepository.findByEmail("oussema@enicar.tn")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(), any())).thenReturn("valid.login.jwt");

        // Act
        AuthResponse response = authService.login(req);

        // Assert
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        assertThat(response.getToken()).isEqualTo("valid.login.jwt");
        assertThat(response.getUser().getEmail()).isEqualTo("oussema@enicar.tn");
    }

    @Test
    @DisplayName("TC-AUTH-04: Should propagate BadCredentialsException when credentials fail")
    void shouldPropagateBadCredentialsException() {
        // Arrange
        LoginRequest req = new LoginRequest();
        req.setEmail("mounib@enicar.tn");
        req.setPassword("WrongPassword");

        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert
        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Bad credentials");

        verify(jwtService, never()).generateToken(any(), any());
    }

    @Test
    @DisplayName("TC-AUTH-05: Should return UserDTO for authenticated email in me()")
    void shouldReturnUserDtoInMe() {
        // Arrange
        String email = "lead@enicar.tn";
        User user = User.builder().id(1L).email(email).firstName("Mohamed").lastName("Babou").role(Role.STUDENT).build();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        UserDTO dto = authService.me(email);

        // Assert
        assertThat(dto).isNotNull();
        assertThat(dto.getEmail()).isEqualTo(email);
    }
}
