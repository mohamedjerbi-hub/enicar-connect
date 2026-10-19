package tn.enicar.enicarconnect.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.enicar.enicarconnect.dto.ResourceDTO;
import tn.enicar.enicarconnect.model.ResourceFile;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.repository.UserRepository;
import tn.enicar.enicarconnect.service.FileStorageService;
import tn.enicar.enicarconnect.service.ResourceService;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ResourceController {

    private final ResourceService resourceService;
    private final FileStorageService fileStorageService;
    private final UserRepository userRepo;

    @GetMapping
    public ResponseEntity<List<ResourceDTO>> getAllResources(Authentication auth) {
        return ResponseEntity.ok(resourceService.getAllResources(resolveId(auth)));
    }

    @PostMapping
    public ResponseEntity<ResourceDTO> uploadResource(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("category") String category,
            @RequestParam("icon") String icon,
            @RequestParam("size") String size,
            @RequestParam(value = "groupId", required = false) Long groupId,
            Authentication auth) {
        return ResponseEntity
                .ok(resourceService.uploadResource(file, title, category, icon, size, resolveId(auth), groupId));
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<ResourceDTO>> getGroupResources(@PathVariable Long groupId, Authentication auth) {
        return ResponseEntity.ok(resourceService.getGroupResources(groupId, resolveId(auth)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResource(@PathVariable Long id, Authentication auth) {
        resourceService.deleteResource(id, resolveId(auth));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        ResourceFile fileEntity = resourceService.getResourceEntity(id);
        try {
            Path filePath = fileStorageService.getFilePath(fileEntity.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + fileEntity.getTitle() + "\"")
                        .body(resource);
            }
            return ResponseEntity.notFound().build();
        } catch (MalformedURLException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Resolve the authenticated user ID from the security context email. O(1) DB
     * index lookup.
     */
    private Long resolveId(Authentication auth) {
        return userRepo.findByEmail(auth.getName())
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }
}
