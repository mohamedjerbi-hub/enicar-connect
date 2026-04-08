package tn.enicar.enicarconnect.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppEventDTO {
    private Long id;
    private String title;
    private String date;
    private String time;
    private String location;
    private String description;
    private String category;
    private String organizer;
    private String color;
    private Integer maxCapacity;

    // Calculated fields for frontend
    private int registeredCount;
    private boolean registered;
    private boolean isOwner;
}
