package tn.enicar.enicarconnect.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.enicar.enicarconnect.dto.CreateGroupRequest;
import tn.enicar.enicarconnect.dto.GroupDTO;
import tn.enicar.enicarconnect.dto.GroupMemberDTO;
import tn.enicar.enicarconnect.model.*;
import tn.enicar.enicarconnect.repository.GroupMemberRepository;
import tn.enicar.enicarconnect.repository.GroupRepository;
import tn.enicar.enicarconnect.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepo;
    private final GroupMemberRepository memberRepo;
    private final UserRepository userRepo;

    @Transactional
    public AppGroup ensureDefaultGroup(String name, String description, User creator) {
        return groupRepo.findByNameIgnoreCase(name).orElseGet(() -> {
            AppGroup created = AppGroup.builder()
                    .name(name)
                    .description(description)
                    .groupType(GroupType.THEMATIC)
                    .kind(GroupKind.DEFAULT)
                    .privacy(GroupPrivacy.PRIVATE)
                    .icon("fas fa-users")
                    .iconColor("var(--gold)")
                    .bannerGradient("linear-gradient(135deg,#0A1628,#1a3060)")
                    .creator(creator)
                    .build();

            AppGroup saved = groupRepo.save(created);
            log.info("Default group created: groupId={} name={}", saved.getId(), saved.getName());
            return saved;
        });
    }

    @Transactional
    public void addUserToGroup(AppGroup group, User user, MemberRole role) {
        if (memberRepo.existsByGroupAndUser(group, user)) {
            return;
        }
        memberRepo.save(GroupMember.builder()
                .group(group)
                .user(user)
                .memberRole(role)
                .build());
        log.info("User added to group: userId={} groupId={} memberRole={}", user.getId(), group.getId(), role);
    }

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

    @Transactional(readOnly = true)
    public List<GroupMemberDTO> getMembers(Long groupId) {
        return memberRepo.findAllByGroupId(groupId).stream()
                .map(m -> {
                    User u = m.getUser();
                    return GroupMemberDTO.builder()
                            .id(m.getId())
                            .userId(u.getId())
                            .fullName(u.getFullName())
                            .initials(u.getInitials())
                            .role(u.getRole().name().toLowerCase())
                            .memberRole(m.getMemberRole().name())
                            .avatarBg(u.getAvatarBg())
                            .avatarColor(u.getAvatarColor())
                            .photoUrl(u.getPhotoUrl())
                            .build();
                })
                .collect(Collectors.toList());
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
