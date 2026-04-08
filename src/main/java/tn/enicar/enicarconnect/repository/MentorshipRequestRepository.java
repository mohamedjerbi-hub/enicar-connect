package tn.enicar.enicarconnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.enicar.enicarconnect.model.MentorshipRequest;
import tn.enicar.enicarconnect.model.User;

import java.util.List;

@Repository
public interface MentorshipRequestRepository extends JpaRepository<MentorshipRequest, Long> {
    List<MentorshipRequest> findByMenteeOrderByCreatedAtDesc(User mentee);

    List<MentorshipRequest> findByMentorOrderByCreatedAtDesc(User mentor);

    boolean existsByMentorAndMenteeAndStatusIn(User mentor, User mentee, List<String> statuses);
}
