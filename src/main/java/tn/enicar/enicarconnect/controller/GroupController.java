package tn.enicar.enicarconnect.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import tn.enicar.enicarconnect.dto.CreateGroupRequest;
import tn.enicar.enicarconnect.dto.GroupDTO;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.repository.UserRepository;
import tn.enicar.enicarconnect.service.GroupService;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final UserRepository userRepo;

    @PostMapping
    public ResponseEntity<GroupDTO> createGroup(@Valid @RequestBody CreateGroupRequest request,
            Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(groupService.createGroup(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<GroupDTO>> getAllGroups(Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(groupService.getAllGroups(userId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<GroupDTO>> getUserGroups(Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(groupService.getUserGroups(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroupDTO> updateGroup(@PathVariable Long id,
            @Valid @RequestBody CreateGroupRequest request,
            Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(groupService.updateGroup(id, userId, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id, Authentication auth) {
        Long userId = getUserId(auth);
        boolean isAdmin = isAdmin(auth);
        groupService.deleteGroup(id, userId, isAdmin);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/toggle-join")
    public ResponseEntity<GroupDTO> toggleJoin(@PathVariable Long id, Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(groupService.toggleJoin(id, userId));
    }

    private Long getUserId(Authentication auth) {
        String email = auth.getName();
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        return user.getId();
    }

    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_ADMIN_STAFF") || a.equals("ROLE_DIRECTION"));
    }
}
