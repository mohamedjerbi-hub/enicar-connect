package tn.enicar.enicarconnect.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.enicar.enicarconnect.dto.ResourceDTO;
import tn.enicar.enicarconnect.model.ResourceFile;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.repository.ResourceFileRepository;
import tn.enicar.enicarconnect.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceFileRepository resourceRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public List<ResourceDTO> getAllResources(Long currentUserId) {
        return resourceRepository.findAllByOrderByUploadDateDesc().stream()
                .map(r -> mapToDTO(r, currentUserId))
                .collect(Collectors.toList());
    }

    public ResourceDTO uploadResource(MultipartFile file, String title, String category, String icon, String size,
            Long currentUserId) {
        User author = userRepository.findById(currentUserId).orElseThrow(() -> new RuntimeException("User not found"));

        // Sauvegarde physique du fichier
        String storedFileName = fileStorageService.storeFile(file);

        ResourceFile resource = ResourceFile.builder()
                .title(title)
                .category(category)
                .icon(icon)
                .fileSize(size)
                .filePath(storedFileName) // on garde juste le nom pour le retrouver
                .author(author)
                .build();

        ResourceFile saved = resourceRepository.save(resource);
        return mapToDTO(saved, currentUserId);
    }

    public void deleteResource(Long id, Long currentUserId) {
        ResourceFile resource = resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        if (!resource.getAuthor().getId().equals(currentUserId)) {
            throw new RuntimeException("Unauthorized");
        }

        // Supprime le fichier physique
        fileStorageService.deleteFile(resource.getFilePath());
        // Supprime l'entité
        resourceRepository.delete(resource);
    }

    public ResourceFile getResourceEntity(Long id) {
        return resourceRepository.findById(id).orElseThrow(() -> new RuntimeException("Resource not found"));
    }

    private ResourceDTO mapToDTO(ResourceFile resource, Long currentUserId) {
        boolean isOwner = resource.getAuthor().getId().equals(currentUserId);

        // Formatage de la date : "15 Jan 2026"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.FRENCH);
        String dateStr = resource.getUploadDate() != null ? resource.getUploadDate().format(formatter)
                : LocalDateTime.now().format(formatter);
        dateStr = dateStr.substring(0, 1).toUpperCase() + dateStr.substring(1); // majuscule mois

        return ResourceDTO.builder()
                .id(resource.getId())
                .title(resource.getTitle())
                .author(resource.getAuthor().getFullName())
                .date(dateStr)
                .size(resource.getFileSize())
                .icon(resource.getIcon())
                .category(resource.getCategory())
                .isOwner(isOwner)
                .build();
    }
}
