package tn.enicar.enicarconnect.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

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
    private String className;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "user_skill_keywords", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "skill", nullable = false)
    private Set<String> skills = new HashSet<>();

    private String avatarColor;
    private String avatarBg;
    private String photoUrl;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Education> educations = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Experience> experiences = new java.util.ArrayList<>();

    @Transient
    private Collection<? extends GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

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
