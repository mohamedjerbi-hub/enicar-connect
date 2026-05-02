package tn.enicar.enicarconnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import tn.enicar.enicarconnect.model.JobOffer;

import java.util.List;

@Repository
public interface JobOfferRepository extends JpaRepository<JobOffer, Long> {
    @Override
    @EntityGraph(attributePaths = {"requiredSkills"})
    List<JobOffer> findAll();

    @EntityGraph(attributePaths = {"author", "requiredSkills"})
    List<JobOffer> findAllByOrderByCreatedAtDesc();
}
