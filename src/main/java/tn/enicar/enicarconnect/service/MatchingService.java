package tn.enicar.enicarconnect.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.enicar.enicarconnect.dto.MatchingResultDTO;
import tn.enicar.enicarconnect.model.JobOffer;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.repository.JobOfferRepository;
import tn.enicar.enicarconnect.repository.UserRepository;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchingService {

    private final UserRepository userRepository;
    private final JobOfferRepository jobOfferRepository;

    public List<MatchingResultDTO> getMatchingResultsForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Set<String> userSkills = normalizeSkills(user.getSkills());
        log.info("Computing matching for userId={} with {} normalized skills", userId, userSkills.size());

        return jobOfferRepository.findAll().stream()
                .map(jobOffer -> buildMatchingResult(jobOffer, userSkills))
                .sorted((left, right) -> Double.compare(right.getCompatibilityScore(), left.getCompatibilityScore()))
                .collect(Collectors.toList());
    }

    private MatchingResultDTO buildMatchingResult(JobOffer jobOffer, Set<String> normalizedUserSkills) {
        Set<String> normalizedRequiredSkills = normalizeSkills(jobOffer.getRequiredSkills());
        Set<String> matchedSkills = new LinkedHashSet<>(normalizedRequiredSkills);
        matchedSkills.retainAll(normalizedUserSkills);

        double compatibilityScore = calculateCompatibilityScore(matchedSkills.size(), normalizedRequiredSkills.size());
        log.info(
                "Matching calculated for jobOfferId={}: matched={} required={} score={}%",
                jobOffer.getId(),
                matchedSkills.size(),
                normalizedRequiredSkills.size(),
                compatibilityScore
        );

        return MatchingResultDTO.builder()
                .jobOfferId(jobOffer.getId())
                .title(jobOffer.getTitle())
                .company(jobOffer.getCompany())
                .requiredSkills(normalizedRequiredSkills)
                .matchedSkills(matchedSkills)
                .compatibilityScore(compatibilityScore)
                .build();
    }

    private double calculateCompatibilityScore(int matchedSkillsCount, int requiredSkillsCount) {
        if (requiredSkillsCount == 0) {
            return 100.0;
        }
        return (matchedSkillsCount * 100.0) / requiredSkillsCount;
    }

    private Set<String> normalizeSkills(Set<String> skills) {
        if (skills == null || skills.isEmpty()) {
            return Collections.emptySet();
        }

        return skills.stream()
                .filter(skill -> skill != null && !skill.isBlank())
                .map(skill -> skill.trim().toLowerCase(Locale.ROOT))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
