package tn.enicar.enicarconnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.enicar.enicarconnect.model.AppGroup;

import java.util.List;

public interface GroupRepository extends JpaRepository<AppGroup, Long> {

    @Query("SELECT g FROM AppGroup g ORDER BY g.createdAt DESC")
    List<AppGroup> findAllOrderByDate();

    @Query("SELECT g FROM AppGroup g JOIN g.members m WHERE m.user.id = :userId ORDER BY g.createdAt DESC")
    List<AppGroup> findGroupsByUserId(@Param("userId") Long userId);

    @Query("SELECT g FROM AppGroup g WHERE g.privacy = 'PUBLIC' ORDER BY g.createdAt DESC")
    List<AppGroup> findAllPublicGroups();
}
