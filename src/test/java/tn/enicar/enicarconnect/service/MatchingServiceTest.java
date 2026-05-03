package tn.enicar.enicarconnect.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.enicar.enicarconnect.dto.MatchingResultDTO;
import tn.enicar.enicarconnect.model.JobOffer;
import tn.enicar.enicarconnect.model.Role;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.repository.JobOfferRepository;
import tn.enicar.enicarconnect.repository.UserRepository;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatchingServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JobOfferRepository jobOfferRepository;

    @InjectMocks
    private MatchingService matchingService;

    private User student;

    @BeforeEach
    void setUp() {
        student = User.builder()
                .id(1L)
                .email("dev@enicar.test")
                .firstName("Test")
                .lastName("User")
                .role(Role.STUDENT)
                .skills(Set.of("Spring Boot", "Angular", "Docker"))
                .build();
    }

    @Test
    void shouldReturn100PercentWhenAllRequiredSkillsMatchJerbiProfile() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));

        JobOffer fullStack = JobOffer.builder()
                .id(10L)
                .title("Stage Full-Stack")
                .company("Vermeg")
                .location("Tunis")
                .type("Stage")
                .description("...")
                .requiredSkills(new LinkedHashSet<>(List.of("Spring Boot", "Angular")))
                .build();

        when(jobOfferRepository.findAll()).thenReturn(List.of(fullStack));

        List<MatchingResultDTO> results = matchingService.getMatchingResultsForUser(1L);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getCompatibilityScore()).isEqualTo(100.0);
        assertThat(results.get(0).getMatchedSkills()).containsExactlyInAnyOrder("spring boot", "angular");
    }

    @Test
    void shouldReturnPartialScoreForDevOpsDockerOnly() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));

        JobOffer devOps = JobOffer.builder()
                .id(11L)
                .title("Ingénieur DevOps")
                .company("Corp")
                .location("Paris")
                .type("CDI")
                .description("...")
                .requiredSkills(new LinkedHashSet<>(List.of("Docker", "CI/CD")))
                .build();

        when(jobOfferRepository.findAll()).thenReturn(List.of(devOps));

        List<MatchingResultDTO> results = matchingService.getMatchingResultsForUser(1L);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getCompatibilityScore()).isEqualTo(50.0);
        assertThat(results.get(0).getMatchedSkills()).containsExactly("docker");
    }

    @Test
    void shouldSortByCompatibilityDescending() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));

        JobOffer weak = JobOffer.builder()
                .id(1L)
                .title("CAD")
                .company("X")
                .location("Y")
                .type("CDI")
                .description("d")
                .requiredSkills(new LinkedHashSet<>(List.of("SOLIDWORKS")))
                .build();

        JobOffer strong = JobOffer.builder()
                .id(2L)
                .title("Web")
                .company("Y")
                .location("Z")
                .type("CDI")
                .description("d")
                .requiredSkills(new LinkedHashSet<>(List.of("Angular")))
                .build();

        when(jobOfferRepository.findAll()).thenReturn(List.of(weak, strong));

        List<MatchingResultDTO> results = matchingService.getMatchingResultsForUser(1L);

        assertThat(results).hasSize(2);
        assertThat(results.get(0).getTitle()).isEqualTo("Web");
        assertThat(results.get(1).getTitle()).isEqualTo("CAD");
    }
}
