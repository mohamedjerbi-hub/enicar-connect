package tn.enicar.enicarconnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.enicar.enicarconnect.model.AppGroup;
import tn.enicar.enicarconnect.model.GroupMember;
import tn.enicar.enicarconnect.model.User;

import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    Optional<GroupMember> findByGroupAndUser(AppGroup group, User user);

    boolean existsByGroupAndUser(AppGroup group, User user);

    int countByGroup(AppGroup group);
}
