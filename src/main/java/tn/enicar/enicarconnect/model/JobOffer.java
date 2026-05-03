package tn.enicar.enicarconnect.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "job_offers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String company;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String type; // CDI, CDD, Stage, etc.

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(columnDefinition = "TEXT")
    private String tags; // Comma separated

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "job_offer_required_skills", joinColumns = @JoinColumn(name = "job_offer_id"))
    @Column(name = "required_skill", nullable = false)
    private Set<String> requiredSkills = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
