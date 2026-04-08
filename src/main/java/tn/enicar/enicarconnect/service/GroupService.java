package tn.enicar.enicarconnect.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.enicar.enicarconnect.dto.CreateGroupRequest;
import tn.enicar.enicarconnect.dto.GroupDTO;
import tn.enicar.enicarconnect.model.*;
import tn.enicar.enicarconnect.repository.GroupMemberRepository;
import tn.enicar.enicarconnect.repository.GroupRepository;
import tn.enicar.enicarconnect.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepo;
    private final GroupMemberRepository memberRepo;
    private final UserRepository userRepo;

    @Transactional
    public GroupDTO createGroup(Long userId, CreateGroupRequest req) {
        User creator = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        AppGroup group = AppGroup.builder()
                .name(req.getName())
                .description(req.getDescription())
                .groupType(GroupType.valueOf(req.getGroupType()))
                .privacy(GroupPrivacy.valueOf(req.getPrivacy()))
                .icon(req.getIcon())
                .iconColor(req.getIconColor())
                .bannerGradient(req.getBannerGradient())
                .creator(creator)
                .build();

        group = groupRepo.save(group);

        // Add creator as ADMIN member
        GroupMember member = GroupMember.builder()
                .group(group)
                .user(creator)
                .memberRole(MemberRole.ADMIN)
                .build();
        memberRepo.save(member);

        return toDTO(group, userId);
    }

    @Transactional(readOnly = true)
    public List<GroupDTO> getAllGroups(Long userId) {
        return groupRepo.findAllOrderByDate().stream()
                .map(g -> toDTO(g, userId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GroupDTO> getUserGroups(Long userId) {
        return groupRepo.findGroupsByUserId(userId).stream()
                .map(g -> toDTO(g, userId))
                .collect(Collectors.toList());
    }

    @Transactional
    public GroupDTO updateGroup(Long groupId, Long userId, CreateGroupRequest req) {
        AppGroup group = groupRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Groupe introuvable"));

        if (!group.getCreator().getId().equals(userId)) {
            throw new RuntimeException("Seul le créateur peut modifier le groupe");
        }

        group.setName(req.getName());
        group.setDescription(req.getDescription());
        group.setGroupType(GroupType.valueOf(req.getGroupType()));
        group.setPrivacy(GroupPrivacy.valueOf(req.getPrivacy()));
        group.setIcon(req.getIcon());
        group.setIconColor(req.getIconColor());
        group.setBannerGradient(req.getBannerGradient());

        group = groupRepo.save(group);
        return toDTO(group, userId);
    }

    @Transactional
    public void deleteGroup(Long groupId, Long userId, boolean isAdmin) {
        AppGroup group = groupRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Groupe introuvable"));

        if (!group.getCreator().getId().equals(userId) && !isAdmin) {
            throw new RuntimeException("Accès refusé");
        }

        groupRepo.delete(group);
    }

    @Transactional
    public GroupDTO toggleJoin(Long groupId, Long userId) {
        AppGroup group = groupRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Groupe introuvable"));
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        memberRepo.findByGroupAndUser(group, user).ifPresentOrElse(
                member -> {
                    if (group.getCreator().getId().equals(userId)) {
                        throw new RuntimeException("Le créateur ne peut pas quitter le groupe");
                    }
                    memberRepo.delete(member);
                },
                () -> {
                    memberRepo.save(GroupMember.builder()
                            .group(group)
                            .user(user)
                            .memberRole(MemberRole.MEMBER)
                            .build());
                });

        return toDTO(group, userId);
    }

    private GroupDTO toDTO(AppGroup group, Long currentUserId) {
        User creator = group.getCreator();

        GroupMember currentMember = memberRepo.findByGroupAndUser(group,
                userRepo.findById(currentUserId).orElse(null)).orElse(null);

        return GroupDTO.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .groupType(group.getGroupType().name())
                .privacy(group.getPrivacy().name())
                .icon(group.getIcon())
                .iconColor(group.getIconColor())
                .bannerGradient(group.getBannerGradient())
                .creatorId(creator.getId())
                .creatorName(creator.getFullName())
                .memberCount(memberRepo.countByGroup(group))
                .joined(currentMember != null)
                .isOwner(creator.getId().equals(currentUserId))
                .myRole(currentMember != null ? currentMember.getMemberRole().name() : null)
                .createdAt(group.getCreatedAt())
                .build();
    }
}
