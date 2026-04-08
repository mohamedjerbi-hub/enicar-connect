package tn.enicar.enicarconnect.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import tn.enicar.enicarconnect.dto.*;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.repository.UserRepository;
import tn.enicar.enicarconnect.service.PostService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final UserRepository userRepo;

    // ─── POSTS CRUD ───────────────────────────────────────

    @PostMapping("/posts")
    public ResponseEntity<PostDTO> createPost(@Valid @RequestBody CreatePostRequest request,
            Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(postService.createPost(userId, request));
    }

    @GetMapping("/posts")
    public ResponseEntity<List<PostDTO>> getAllPosts(Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(postService.getAllPosts(userId));
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<PostDTO> getPost(@PathVariable Long id, Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(postService.getPost(id, userId));
    }

    @PutMapping("/posts/{id}")
    public ResponseEntity<PostDTO> updatePost(@PathVariable Long id,
            @Valid @RequestBody CreatePostRequest request,
            Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(postService.updatePost(id, userId, request));
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id, Authentication auth) {
        Long userId = getUserId(auth);
        boolean isAdmin = isAdmin(auth);
        postService.deletePost(id, userId, isAdmin);
        return ResponseEntity.noContent().build();
    }

    // ─── LIKES ────────────────────────────────────────────

    @PostMapping("/posts/{id}/like")
    public ResponseEntity<PostDTO> toggleLike(@PathVariable Long id, Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(postService.toggleLike(id, userId));
    }

    // ─── COMMENTS ─────────────────────────────────────────

    @PostMapping("/posts/{id}/comments")
    public ResponseEntity<CommentDTO> addComment(@PathVariable Long id,
            @Valid @RequestBody CreateCommentRequest request,
            Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(postService.addComment(id, userId, request.getText()));
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id, Authentication auth) {
        Long userId = getUserId(auth);
        boolean isAdmin = isAdmin(auth);
        postService.deleteComment(id, userId, isAdmin);
        return ResponseEntity.noContent().build();
    }

    // ─── REPORTING & MODERATION ───────────────────────────

    @PostMapping("/posts/{id}/report")
    public ResponseEntity<Map<String, String>> reportPost(@PathVariable Long id,
            @Valid @RequestBody ReportRequest request,
            Authentication auth) {
        Long userId = getUserId(auth);
        postService.reportPost(id, userId, request.getReason());
        return ResponseEntity.ok(Map.of("message", "Signalement envoyé"));
    }

    @GetMapping("/posts/reported")
    @PreAuthorize("hasAnyRole('ADMIN_STAFF', 'DIRECTION')")
    public ResponseEntity<List<PostDTO>> getReportedPosts(Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(postService.getReportedPosts(userId));
    }

    @PutMapping("/posts/{id}/moderate")
    @PreAuthorize("hasAnyRole('ADMIN_STAFF', 'DIRECTION')")
    public ResponseEntity<PostDTO> moderatePost(@PathVariable Long id,
            @RequestBody Map<String, String> body,
            Authentication auth) {
        Long userId = getUserId(auth);
        String action = body.getOrDefault("action", "DISMISS");
        return ResponseEntity.ok(postService.moderatePost(id, action, userId));
    }

    // ─── HELPERS ──────────────────────────────────────────

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
