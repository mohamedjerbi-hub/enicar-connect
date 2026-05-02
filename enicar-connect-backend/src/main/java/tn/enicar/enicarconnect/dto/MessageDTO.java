package tn.enicar.enicarconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDTO {
    private Long id;
    private Long groupId;
    private Long senderId;
    private String senderName;
    private String senderInitials;
    private String content;
    private LocalDateTime timestamp;
}

