package tn.enicar.enicarconnect.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupMemberDTO {
    private Long id;
    private Long userId;
    private String fullName;
    private String initials;
    private String role;
    private String memberRole;
    private String avatarBg;
    private String avatarColor;
    private String photoUrl;
}
