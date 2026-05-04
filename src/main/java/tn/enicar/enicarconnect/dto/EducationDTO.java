package tn.enicar.enicarconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EducationDTO {
    private Long id;
    private String degree;
    private String school;
    private String period;
    private String icon;
}
