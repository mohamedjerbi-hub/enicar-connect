package tn.enicar.enicarconnect.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDTO {

    private Long id;

    private Long authorId;
    private String authorName;
    private String authorInitials;
    private String authorRole;
    private String authorAvatarColor;
    private String authorAvatarBg;

    private String text;
    private LocalDateTime createdAt;
}
