package tn.enicar.enicarconnect.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobDTO {
    private Long id;
    private String title;
    private String company;
    private String location;
    private String type;
    private String description;
    private List<String> tags;
    private String posted; // Date relative string like "Il y a 2 jours"

    // Calculated boolean fields for the current user
    private boolean applied;
    private boolean isOwner;
}
