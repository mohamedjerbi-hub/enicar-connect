package tn.enicar.enicarconnect.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.enicar.enicarconnect.dto.UserDTO;
import tn.enicar.enicarconnect.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    public ResponseEntity<List<UserDTO>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/directory")
    public ResponseEntity<List<UserDTO>> getDirectory() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(
            @PathVariable Long id,
            @RequestBody UserDTO dto,
            Authentication authentication) {
        // Only owner or USER_MANAGE can update
        UserDTO existing = userService.findById(id);
        boolean isOwner = existing.getEmail().equals(authentication.getName());
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("USER_MANAGE"));

        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(userService.update(id, dto));
    }
}
