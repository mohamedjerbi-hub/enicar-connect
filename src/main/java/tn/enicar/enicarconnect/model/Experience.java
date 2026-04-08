package tn.enicar.enicarconnect.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_experiences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Experience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String company;

    @Column(nullable = false)
    private String period;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String icon;
}
