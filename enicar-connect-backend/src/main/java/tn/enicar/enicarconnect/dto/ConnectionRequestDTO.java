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
public class ConnectionRequestDTO {
    private Long id;
    private UserDTO sender;
    private String status;
    private LocalDateTime timestamp;
}
