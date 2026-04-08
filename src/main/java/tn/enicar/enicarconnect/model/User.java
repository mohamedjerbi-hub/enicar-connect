package tn.enicar.enicarconnect.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String phone;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private String website;
    private String linkedin;
    private String github;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private String department;
    private String level;

    private String avatarColor;
    private String avatarBg;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // --- Méthodes utilitaires ---

    @Transient
    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Transient
    public String getInitials() {
        String f = (firstName != null && !firstName.isEmpty()) ? firstName.substring(0, 1) : "";
        String l = (lastName != null && !lastName.isEmpty()) ? lastName.substring(0, 1) : "";
        return (f + l).toUpperCase();
    }
}
