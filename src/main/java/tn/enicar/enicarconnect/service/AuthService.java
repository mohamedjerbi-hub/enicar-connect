package tn.enicar.enicarconnect.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.enicar.enicarconnect.config.RolePermissionConfig;
import tn.enicar.enicarconnect.dto.*;
import tn.enicar.enicarconnect.model.Role;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.repository.UserRepository;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RolePermissionConfig rolePermissionConfig;

    /**
     * Inscription d'un nouvel utilisateur.
     */
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Un compte avec cet email existe déjà");
        }

        Role role = parseRole(request.getRole());

        // Couleurs par défaut selon le rôle
        String avatarColor = getRoleColor(role);
        String avatarBg = getRoleBg(role);

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(role)
                .department(request.getDepartment())
                .level(request.getLevel())
                .avatarColor(avatarColor)
                .avatarBg(avatarBg)
                .build();

        userRepository.save(user);

        String token = generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .user(toDTO(user))
                .build();
    }

    /**
     * Connexion par email / mot de passe.
     */
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        String token = generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .user(toDTO(user))
                .build();
    }

    /**
     * Retourne les infos de l'utilisateur connecté.
     */
    public UserDTO me(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        return toDTO(user);
    }

    // --- Helpers ---

    private String generateToken(User user) {
        return jwtService.generateToken(user.getEmail(), Map.of(
                "role", user.getRole().name(),
                "userId", user.getId()));
    }

    public UserDTO toDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .bio(user.getBio())
                .website(user.getWebsite())
                .linkedin(user.getLinkedin())
                .github(user.getGithub())
                .role(user.getRole().name().toLowerCase())
                .department(user.getDepartment())
                .level(user.getLevel())
                .initials(user.getInitials())
                .fullName(user.getFullName())
                .avatarColor(user.getAvatarColor())
                .avatarBg(user.getAvatarBg())
                .permissions(rolePermissionConfig.getPermissionNames(user.getRole()))
                .build();
    }

    private Role parseRole(String roleStr) {
        if (roleStr == null || roleStr.isBlank())
            return Role.STUDENT;
        try {
            return Role.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Role.STUDENT;
        }
    }

    private String getRoleColor(Role role) {
        return switch (role) {
            case STUDENT -> "var(--role-student)";
            case TEACHER -> "var(--role-prof)";
            case ADMIN_STAFF -> "var(--role-admin)";
            case DIRECTION -> "var(--role-direction)";
            case ALUMNI -> "var(--role-alumni)";
        };
    }

    private String getRoleBg(Role role) {
        return switch (role) {
            case STUDENT -> "rgba(99,102,241,.15)";
            case TEACHER -> "rgba(168,85,247,.15)";
            case ADMIN_STAFF -> "rgba(34,197,94,.15)";
            case DIRECTION -> "rgba(234,179,8,.15)";
            case ALUMNI -> "rgba(249,115,22,.15)";
        };
    }
}
