package tn.enicar.enicarconnect.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReportRequest {

    @NotBlank(message = "La raison du signalement est requise")
    private String reason;
}
