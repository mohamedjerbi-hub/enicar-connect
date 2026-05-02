package tn.enicar.enicarconnect.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.enicar.enicarconnect.dto.MatchingResultDTO;
import tn.enicar.enicarconnect.model.JobOffer;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.repository.JobOfferRepository;
import tn.enicar.enicarconnect.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatchingServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JobOfferRepository jobOfferRepository;

    @InjectMocks
    private MatchingService matchingService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("student@enicar.tn")
                .skills(Set.of("Java", "Spring Boot", "Angular"))
                .build();
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> matchingService.getMatchingResultsForUser(99L)
        );

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void shouldReturnMatchesSortedByCompatibilityDescending() {
        JobOffer goodMatch = JobOffer.builder()
                .id(10L)
                .title("Backend Internship")
                .company("TechOne")
                .requiredSkills(Set.of("java", "spring boot"))
                .build();

        JobOffer mediumMatch = JobOffer.builder()
                .id(11L)
                .title("Frontend Internship")
                .company("TechTwo")
                .requiredSkills(Set.of("angular", "typescript"))
                .build();

        JobOffer noMatch = JobOffer.builder()
                .id(12L)
                .title("Data Engineer")
                .company("TechThree")
                .requiredSkills(Set.of("python", "spark"))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(jobOfferRepository.findAll()).thenReturn(List.of(noMatch, mediumMatch, goodMatch));

        List<MatchingResultDTO> results = matchingService.getMatchingResultsForUser(1L);

        assertEquals(3, results.size());
        assertEquals(10L, results.get(0).getJobOfferId());
        assertEquals(11L, results.get(1).getJobOfferId());
        assertEquals(12L, results.get(2).getJobOfferId());
        assertEquals(100.0, results.get(0).getCompatibilityScore());
        assertEquals(50.0, results.get(1).getCompatibilityScore());
        assertEquals(0.0, results.get(2).getCompatibilityScore());
    }

    @Test
    void shouldReturnHundredPercentWhenOfferHasNoRequiredSkills() {
        JobOffer openOffer = JobOffer.builder()
                .id(13L)
                .title("General Internship")
                .company("OpenCorp")
                .requiredSkills(Set.of())
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(jobOfferRepository.findAll()).thenReturn(List.of(openOffer));

        List<MatchingResultDTO> results = matchingService.getMatchingResultsForUser(1L);

        assertEquals(1, results.size());
        assertEquals(100.0, results.get(0).getCompatibilityScore());
        assertEquals(Set.of(), results.get(0).getMatchedSkills());
    }

    @Test
    void shouldMatchSkillsCaseInsensitivelyAndIgnoreBlanks() {
        User mixedCaseUser = User.builder()
                .id(2L)
                .email("alumni@enicar.tn")
                .skills(Set.of("  Java  ", "SPRING BOOT", ""))
                .build();

        JobOffer offer = JobOffer.builder()
                .id(14L)
                .title("Java Backend")
                .company("CaseTech")
                .requiredSkills(Set.of("java", "spring boot", "docker"))
                .build();

        when(userRepository.findById(2L)).thenReturn(Optional.of(mixedCaseUser));
        when(jobOfferRepository.findAll()).thenReturn(List.of(offer));

        List<MatchingResultDTO> results = matchingService.getMatchingResultsForUser(2L);

        assertEquals(1, results.size());
        assertEquals(14L, results.get(0).getJobOfferId());
        assertEquals(66.66666666666667, results.get(0).getCompatibilityScore(), 0.0001);
    }
}
