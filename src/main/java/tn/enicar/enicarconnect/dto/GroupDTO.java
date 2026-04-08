package tn.enicar.enicarconnect.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class GroupDTO {

    private Long id;
    private String name;
    private String description;
    private String groupType;
    private String privacy;

    private String icon;
    private String iconColor;
    private String bannerGradient;

    private Long creatorId;
    private String creatorName;

    private int memberCount;
    private boolean joined; // Is current user a member?
    private boolean isOwner; // Is current user the creator?
    private String myRole; // MEMBER, MODERATOR, ADMIN, or null

    private LocalDateTime createdAt;
}
