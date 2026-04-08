package tn.enicar.enicarconnect.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorshipDTO {
    private Long id;

    // Partner infomations (Mentor si courant = Mentee, Mentee si courant = Mentor)
    private Long partnerId;
    private String partnerName;
    private String partnerRole;
    private String partnerDepartment;

    private String objective;
    private String status;
    private String date;
}
