package tn.enicar.enicarconnect.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.enicar.enicarconnect.dto.CreateGroupRequest;
import tn.enicar.enicarconnect.dto.GroupDTO;
import tn.enicar.enicarconnect.model.AppGroup;
import tn.enicar.enicarconnect.model.GroupPrivacy;
import tn.enicar.enicarconnect.model.GroupType;
import tn.enicar.enicarconnect.model.GroupMember;
import tn.enicar.enicarconnect.model.MemberRole;
import tn.enicar.enicarconnect.model.Role;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.repository.GroupMemberRepository;
import tn.enicar.enicarconnect.repository.GroupRepository;
import tn.enicar.enicarconnect.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock
    private GroupRepository groupRepo;

    @Mock
    private GroupMemberRepository memberRepo;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private GroupService groupService;

    @Test
    void createGroupShouldPersistGroupAndAddCreatorAsAdmin() {
        User creator = User.builder()
                .id(5L)
                .email("c@enicar.test")
                .firstName("Cr")
                .lastName("Ea")
                .role(Role.STUDENT)
                .build();

        when(userRepo.findById(5L)).thenReturn(Optional.of(creator));

        AppGroup saved = AppGroup.builder()
                .id(100L)
                .name("Projet Lab")
                .description("d")
                .groupType(GroupType.PROJECT)
                .privacy(GroupPrivacy.PUBLIC)
                .creator(creator)
                .build();
        when(groupRepo.save(any(AppGroup.class))).thenReturn(saved);
        GroupMember adminLink = GroupMember.builder()
                .group(saved)
                .user(creator)
                .memberRole(MemberRole.ADMIN)
                .build();
        when(memberRepo.findByGroupAndUser(saved, creator)).thenReturn(Optional.of(adminLink));
        when(memberRepo.countByGroup(saved)).thenReturn(1);

        CreateGroupRequest req = new CreateGroupRequest();
        req.setName("Projet Lab");
        req.setDescription("d");
        req.setGroupType("PROJECT");
        req.setPrivacy("PUBLIC");

        GroupDTO dto = groupService.createGroup(5L, req);

        assertThat(dto.getName()).isEqualTo("Projet Lab");
        assertThat(dto.isJoined()).isTrue();
        assertThat(dto.getIsOwner()).isTrue();

        ArgumentCaptor<GroupMember> captor = ArgumentCaptor.forClass(GroupMember.class);
        verify(memberRepo).save(captor.capture());
        assertThat(captor.getValue().getMemberRole()).isEqualTo(MemberRole.ADMIN);
    }

    @Test
    void toggleJoinShouldThrowWhenCreatorLeaves() {
        User creator = User.builder().id(1L).email("x@y").firstName("A").lastName("B").role(Role.TEACHER).build();
        AppGroup group = AppGroup.builder()
                .id(20L)
                .name("G")
                .creator(creator)
                .build();

        when(groupRepo.findById(20L)).thenReturn(Optional.of(group));
        when(userRepo.findById(1L)).thenReturn(Optional.of(creator));
        when(memberRepo.findByGroupAndUser(group, creator))
                .thenReturn(Optional.of(GroupMember.builder()
                        .group(group)
                        .user(creator)
                        .memberRole(MemberRole.ADMIN)
                        .build()));

        assertThatThrownBy(() -> groupService.toggleJoin(20L, 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("créateur");
    }
}
