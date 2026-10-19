package tn.enicar.enicarconnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.enicar.enicarconnect.model.AppGroup;
import tn.enicar.enicarconnect.model.GroupMember;
import tn.enicar.enicarconnect.model.User;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    Optional<GroupMember> findByGroupAndUser(AppGroup group, User user);

    List<GroupMember> findAllByGroupId(Long groupId);

    boolean existsByGroupAndUser(AppGroup group, User user);

    int countByGroup(AppGroup group);
}
