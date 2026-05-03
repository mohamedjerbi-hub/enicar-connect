package tn.enicar.enicarconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchingResultDTO {
    private Long jobOfferId;
    private String title;
    private String company;
    private Set<String> requiredSkills;
    private Set<String> matchedSkills;
    private double compatibilityScore;
}
