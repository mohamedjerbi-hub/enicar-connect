package tn.enicar.enicarconnect.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_educations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Education {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String degree;

    @Column(nullable = false)
    private String school;

    @Column(nullable = false)
    private String period;

    private String icon;
}
