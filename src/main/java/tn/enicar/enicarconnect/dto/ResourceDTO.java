package tn.enicar.enicarconnect.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceDTO {
    private Long id;
    private String title;
    private String author;
    private String date; // "15 Jan 2026"
    private String size; // "3.2 MB"
    private String icon;
    private String category;

    // Front-end specific fields
    private boolean isOwner;
}
