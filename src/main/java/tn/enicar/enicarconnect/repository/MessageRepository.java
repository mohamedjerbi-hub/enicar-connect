package tn.enicar.enicarconnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.enicar.enicarconnect.model.Message;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByRecipientGroupIdOrderByTimestampAsc(Long groupId);
}

