package tn.enicar.enicarconnect.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("JwtService Unit Tests (Senior QA)")
class JwtServiceTest {

    private JwtService jwtService;
    private final String secret = "enicarConnectSecretKeyTestingMustBeAtLeast32BytesLongForHmacSha256!";
    private final long expirationMs = 3600000L; // 1 hour

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", secret);
        ReflectionTestUtils.setField(jwtService, "expirationMs", expirationMs);
    }

    @Test
    @DisplayName("TC-JWT-01: Should generate valid JWT and extract correct email subject")
    void shouldGenerateTokenAndExtractEmail() {
        String email = "mohamed.babou@enicar.tn";
        Map<String, Object> claims = Map.of("role", "STUDENT", "userId", 101L);

        String token = jwtService.generateToken(email, claims);

        assertThat(token).isNotBlank();
        String extractedEmail = jwtService.extractEmail(token);
        assertThat(extractedEmail).isEqualTo(email);
    }

    @Test
    @DisplayName("TC-JWT-02: Should validate token when email matches and token is not expired")
    void shouldValidateTokenWhenEmailMatches() {
        String email = "oussema.mejri@enicar.tn";
        String token = jwtService.generateToken(email, Map.of("role", "STUDENT"));

        boolean isValid = jwtService.isTokenValid(token, email);

        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("TC-JWT-03: Should reject token when email does not match")
    void shouldRejectTokenWhenEmailMismatches() {
        String email = "mounib.madyouni@enicar.tn";
        String otherEmail = "impostor@external.com";
        String token = jwtService.generateToken(email, Map.of());

        boolean isValid = jwtService.isTokenValid(token, otherEmail);

        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("TC-JWT-04: Should throw ExpiredJwtException when token has expired")
    void shouldThrowWhenTokenIsExpired() {
        // Set expirationMs to negative value to simulate expired token
        ReflectionTestUtils.setField(jwtService, "expirationMs", -1000L);
        String email = "expired@enicar.tn";

        String expiredToken = jwtService.generateToken(email, Map.of());

        assertThatThrownBy(() -> jwtService.isTokenValid(expiredToken, email))
                .isInstanceOf(io.jsonwebtoken.ExpiredJwtException.class);
    }
}
