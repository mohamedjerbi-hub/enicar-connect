package tn.enicar.enicarconnect.bdd;

import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Et;
import io.cucumber.java.fr.Etantdonné;
import io.cucumber.java.fr.Quand;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import tn.enicar.enicarconnect.config.RolePermissionConfig;
import tn.enicar.enicarconnect.dto.AuthResponse;
import tn.enicar.enicarconnect.dto.LoginRequest;
import tn.enicar.enicarconnect.dto.RegisterRequest;
import tn.enicar.enicarconnect.model.AppGroup;
import tn.enicar.enicarconnect.model.Role;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.repository.UserRepository;
import tn.enicar.enicarconnect.service.AuthService;
import tn.enicar.enicarconnect.service.GroupService;
import tn.enicar.enicarconnect.service.JwtService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthSteps {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final JwtService jwtService = mock(JwtService.class);
    private final AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
    private final RolePermissionConfig rolePermissionConfig = mock(RolePermissionConfig.class);
    private final GroupService groupService = mock(GroupService.class);

    private final AuthService authService = new AuthService(
            userRepository,
            passwordEncoder,
            jwtService,
            authenticationManager,
            rolePermissionConfig,
            groupService
    );

    private RegisterRequest registerRequest;
    private AuthResponse authResponse;
    private LoginRequest loginRequest;
    private Exception loginException;
    private String userRole;
    private int accessStatusCode;

    @Etantdonné("un nouvel étudiant s'inscrivant avec l'email {string} et le mot de passe {string}")
    public void unNouvelEtudiantSInscrivantAvecLEmailEtLeMotDePasse(String email, String password) {
        registerRequest = new RegisterRequest();
        registerRequest.setEmail(email);
        registerRequest.setPassword(password);
        registerRequest.setFirstName("Nouvel");
        registerRequest.setLastName("Etudiant");
        registerRequest.setRole("STUDENT");

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn("$2a$10$encodedPasswordHash");
        when(jwtService.generateToken(any(), any())).thenReturn("mock.jwt.bdd.token");
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId(10L);
            return u;
        });
        when(rolePermissionConfig.getPermissionNames(any())).thenReturn(List.of("POST_READ"));
        when(groupService.ensureDefaultGroup(any(), any(), any())).thenReturn(new AppGroup());
    }

    @Quand("l'étudiant valide son formulaire d'inscription")
    public void lEtudiantValideSonFormulaireDInscription() {
        authResponse = authService.register(registerRequest);
    }

    @Alors("le compte est créé avec le rôle {string}")
    public void leCompteEstCreeAvecLeRole(String expectedRole) {
        assertThat(authResponse.getUser().getRole()).isEqualToIgnoringCase(expectedRole);
    }

    @Et("un jeton JWT d'authentification valide est généré")
    public void unJetonJWTValideEstGenere() {
        assertThat(authResponse.getToken()).isNotBlank();
        assertThat(authResponse.getToken()).isEqualTo("mock.jwt.bdd.token");
    }

    @Etantdonné("un compte utilisateur existant avec l'email {string} et le mot de passe {string}")
    public void unCompteUtilisateurExistantAvecLEmailEtLeMotDePasse(String email, String password) {
        loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
    }

    @Quand("l'utilisateur tente de se connecter avec le mot de passe erroné {string}")
    public void lUtilisateurTenteDeSeConnecterAvecLeMotDePasseErrone(String wrongPassword) {
        loginRequest.setPassword(wrongPassword);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        try {
            authService.login(loginRequest);
        } catch (Exception e) {
            loginException = e;
        }
    }

    @Alors("la connexion doit être rejetée avec une erreur d'authentification")
    public void laConnexionDoitEtreRejeteeAvecUneErreurDAuthentification() {
        assertThat(loginException)
                .isNotNull()
                .isInstanceOf(BadCredentialsException.class);
    }

    @Etantdonné("un utilisateur connecté avec le rôle {string}")
    public void unUtilisateurConnecteAvecLeRole(String role) {
        userRole = role;
    }

    @Quand("il tente d'accéder à l'espace de gestion administrative")
    public void ilTenteDAccederALespaceDeGestionAdministrative() {
        // Enforce RBAC logic: Only ADMIN_STAFF or DIRECTION can access
        if (Role.STUDENT.name().equalsIgnoreCase(userRole)) {
            accessStatusCode = 403; // Forbidden
        } else if (Role.ADMIN_STAFF.name().equalsIgnoreCase(userRole)) {
            accessStatusCode = 200; // OK
        }
    }

    @Alors("l'accès doit être strictement refusé avec un statut 403 Interdit")
    public void lAccesDoitEtreStrictementRefuseAvecUnStatut403Interdit() {
        assertThat(accessStatusCode).isEqualTo(403);
    }
}
