package tn.enicar.enicarconnect.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.enicar.enicarconnect.config.RolePermissionConfig;
import tn.enicar.enicarconnect.dto.*;
import tn.enicar.enicarconnect.model.AppGroup;
import tn.enicar.enicarconnect.model.GroupKind;
import tn.enicar.enicarconnect.model.Role;
import tn.enicar.enicarconnect.model.MemberRole;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.repository.UserRepository;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RolePermissionConfig rolePermissionConfig;
    private final GroupService groupService;

    /**
     * Inscription d'un nouvel utilisateur.
     */
    public AuthResponse register(RegisterRequest request) {
        log.info("Register attempt: email={} role={}", request.getEmail(), request.getRole());
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Register rejected: email already exists ({})", request.getEmail());
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
                .className(resolveClassName(role, request))
                .avatarColor(avatarColor)
                .avatarBg(avatarBg)
                .build();

        userRepository.save(user);
        log.info("Register success: userId={} email={}", user.getId(), user.getEmail());

        assignDefaultGroups(user);

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
        log.info("Login attempt: email={}", request.getEmail());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        String token = generateToken(user);
        log.info("Login success: userId={} email={}", user.getId(), user.getEmail());
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
                .skills(user.getSkills())
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

    private void assignDefaultGroups(User user) {
        if (user.getRole() == Role.ADMIN_STAFF || user.getRole() == Role.DIRECTION) {
            AppGroup admin = groupService.ensureDefaultGroup(
                    "Administration",
                    "Groupe par défaut pour le personnel administratif et la direction.",
                    user
            );
            groupService.addUserToGroup(admin, user, MemberRole.MEMBER);
            return;
        }

        if (user.getRole() == Role.TEACHER) {
            AppGroup teachers = groupService.ensureDefaultGroup(
                    "Corps Enseignant",
                    "Groupe par défaut réservé aux enseignants.",
                    user
            );
            groupService.addUserToGroup(teachers, user, MemberRole.MEMBER);
            return;
        }

        if (user.getRole() == Role.STUDENT) {
            String className = user.getClassName() != null && !user.getClassName().isBlank()
                    ? user.getClassName().trim()
                    : "Classe";
            AppGroup classGroup = groupService.ensureDefaultGroup(
                    "Classe " + className,
                    "Groupe de classe créé automatiquement lors de l'inscription.",
                    user
            );
            groupService.addUserToGroup(classGroup, user, MemberRole.MEMBER);
        }
    }

    private String resolveClassName(Role role, RegisterRequest request) {
        if (role != Role.STUDENT) {
            return request.getClassName();
        }
        if (request.getClassName() != null && !request.getClassName().isBlank()) {
            return request.getClassName().trim();
        }
        // fallback simple à partir des champs existants (évite de casser le front)
        String level = request.getLevel() != null ? request.getLevel().trim() : "";
        String dep = request.getDepartment() != null ? request.getDepartment().trim() : "";
        String merged = (level + " " + dep).trim();
        return merged.isBlank() ? "2ème Info" : merged;
    }
}
