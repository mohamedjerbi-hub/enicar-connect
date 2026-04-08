package tn.enicar.enicarconnect.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateGroupRequest {

    @NotBlank(message = "Le nom du groupe est requis")
    private String name;

    private String description;

    private String groupType = "THEMATIC"; // FILIERE, PROMOTION, CLUB, THEMATIC, PROJECT
    private String privacy = "PUBLIC"; // PUBLIC, PRIVATE, INVITE_ONLY

    private String icon = "fas fa-users";
    private String iconColor = "#C9A84C";
    private String bannerGradient = "linear-gradient(135deg,#0A1628,#1a3060)";
}
