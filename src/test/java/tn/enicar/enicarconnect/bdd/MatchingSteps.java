package tn.enicar.enicarconnect.bdd;

import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Et;
import io.cucumber.java.fr.Etantdonné;
import io.cucumber.java.fr.Quand;
import tn.enicar.enicarconnect.dto.MatchingResultDTO;
import tn.enicar.enicarconnect.model.JobOffer;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.repository.JobOfferRepository;
import tn.enicar.enicarconnect.repository.UserRepository;
import tn.enicar.enicarconnect.service.MatchingService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MatchingSteps {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final JobOfferRepository jobOfferRepository = mock(JobOfferRepository.class);
    private final MatchingService matchingService = new MatchingService(userRepository, jobOfferRepository);

    private User student;
    private JobOffer jobOffer;
    private List<MatchingResultDTO> results;

    @Etantdonné("un étudiant possédant les compétences {string}")
    public void unEtudiantPossedantLesCompetences(String skillsStr) {
        Set<String> skills = Arrays.stream(skillsStr.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());

        student = User.builder()
                .id(1L)
                .email("student@enicar.tn")
                .skills(skills)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
    }

    @Et("une offre d'emploi intitulée {string} exigeant {string}")
    public void uneOffreDEmploiIntituleeExigeant(String title, String requiredSkillsStr) {
        Set<String> requiredSkills = Arrays.stream(requiredSkillsStr.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());

        jobOffer = JobOffer.builder()
                .id(101L)
                .title(title)
                .company("ENICAR Partner")
                .requiredSkills(requiredSkills)
                .build();

        when(jobOfferRepository.findAll()).thenReturn(List.of(jobOffer));
    }

    @Quand("le système calcule le score de matching pour l'étudiant")
    public void leSystemeCalculeLeScoreDeMatchingPourLEtudiant() {
        results = matchingService.getMatchingResultsForUser(1L);
    }

    @Alors("le score de compatibilité calculé doit être de {double}%")
    public void leScoreDeCompatibiliteCalculeDoitEtreDe(Double expectedScore) {
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getCompatibilityScore()).isEqualTo(expectedScore);
    }

    @Et("les compétences correspondantes doivent être {string}")
    public void lesCompetencesCorrespondantesDoiventEtre(String expectedSkillsStr) {
        List<String> expectedSkills = Arrays.stream(expectedSkillsStr.split(","))
                .map(String::trim)
                .toList();

        assertThat(results.get(0).getMatchedSkills()).containsAll(expectedSkills);
    }
}
