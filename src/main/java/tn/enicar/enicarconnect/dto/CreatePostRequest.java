package tn.enicar.enicarconnect.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class CreatePostRequest {

    @NotBlank(message = "Le contenu de la publication est requis")
    private String body;

    private Long groupId;

    private String visibility = "PUBLIC"; // PUBLIC, GROUP, PRIVATE

    private List<String> hashtags;

    private List<Long> mentionedUserIds;

    private List<String> mediaUrls;
}
