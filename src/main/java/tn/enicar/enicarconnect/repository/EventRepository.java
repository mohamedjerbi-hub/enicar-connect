package tn.enicar.enicarconnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.enicar.enicarconnect.model.AppEvent;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<AppEvent, Long> {
    List<AppEvent> findAllByOrderByDateAscTimeAsc();
}
