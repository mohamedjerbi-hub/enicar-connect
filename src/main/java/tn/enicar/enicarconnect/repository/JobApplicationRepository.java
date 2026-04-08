package tn.enicar.enicarconnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.enicar.enicarconnect.model.JobApplication;
import tn.enicar.enicarconnect.model.JobOffer;
import tn.enicar.enicarconnect.model.User;

import java.util.Optional;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    boolean existsByJobOfferAndApplicant(JobOffer jobOffer, User applicant);

    Optional<JobApplication> findByJobOfferAndApplicant(JobOffer jobOffer, User applicant);
}
