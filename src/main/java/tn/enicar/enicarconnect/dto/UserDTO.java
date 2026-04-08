package tn.enicar.enicarconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String bio;
    private String website;
    private String linkedin;
    private String github;
    private String role;
    private String department;
    private String level;
    private String initials;
    private String fullName;
    private String avatarColor;
    private String avatarBg;
    private List<String> permissions;
}
