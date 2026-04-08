package tn.enicar.enicarconnect.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCommentRequest {

    @NotBlank(message = "Le commentaire ne peut pas être vide")
    private String text;
}
