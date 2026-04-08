package tn.enicar.enicarconnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tn.enicar.enicarconnect.model.ConnectionRequest;
import tn.enicar.enicarconnect.model.User;

import java.util.List;

@Repository
public interface ConnectionRequestRepository extends JpaRepository<ConnectionRequest, Long> {

    List<ConnectionRequest> findByReceiverAndStatus(User receiver, String status);

    @Query("SELECT cr.sender FROM ConnectionRequest cr WHERE cr.receiver = :user AND cr.status = 'ACCEPTED' " +
            "UNION " +
            "SELECT cr.receiver FROM ConnectionRequest cr WHERE cr.sender = :user AND cr.status = 'ACCEPTED'")
    List<User> findAcceptedConnections(User user);

    boolean existsBySenderAndReceiver(User sender, User receiver);
}
