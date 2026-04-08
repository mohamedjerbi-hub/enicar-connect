package tn.enicar.enicarconnect.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PostDTO {

    private Long id;

    // Author info
    private Long authorId;
    private String authorName;
    private String authorInitials;
    private String authorRole;
    private String authorAvatarColor;
    private String authorAvatarBg;

    private Long groupId;

    private String body;
    private String visibility;
    private List<String> hashtags;
    private List<String> mediaUrls;
    private List<MentionDTO> mentions;

    private int likesCount;
    private boolean likedByMe;
    private int commentsCount;
    private List<CommentDTO> comments;
    private int reportsCount;
    private boolean moderated;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    public static class MentionDTO {
        private Long id;
        private String name;
        private String initials;
    }
}
