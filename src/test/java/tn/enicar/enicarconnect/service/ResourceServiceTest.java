package tn.enicar.enicarconnect.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import tn.enicar.enicarconnect.model.ResourceFile;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.repository.ResourceFileRepository;
import tn.enicar.enicarconnect.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {
    @Mock ResourceFileRepository resources;
    @Mock UserRepository users;
    @Mock FileStorageService storage;
    @InjectMocks ResourceService service;

    private User user(Long id) {
        User u = new User();
        u.setId(id);
        u.setFirstName("Test");
        u.setLastName("Author");
        return u;
    }

    @Test
    void listsAndUploadsResources() {
        User author = user(1L);
        ResourceFile resource = ResourceFile.builder().id(4L).title("Cours").author(author)
                .fileSize("2 MB").category("Cours").icon("book").filePath("cours.pdf")
                .uploadDate(LocalDateTime.of(2026, 1, 15, 10, 0)).build();
        when(resources.findAllByOrderByUploadDateDesc()).thenReturn(List.of(resource));
        assertEquals("15 janv. 2026", service.getAllResources(1L).get(0).getDate());

        when(users.findById(1L)).thenReturn(Optional.of(author));
        when(storage.storeFile(any())).thenReturn("stored.pdf");
        when(resources.save(any(ResourceFile.class))).thenReturn(resource);
        var file = new MockMultipartFile("file", "cours.pdf", "application/pdf", "data".getBytes());
        assertEquals("Cours", service.uploadResource(file, "Cours", "Cours", "book", "2 MB", 1L, null).getTitle());
    }

    @Test
    void handlesMissingDateAndAuthorization() {
        User author = user(1L);
        ResourceFile resource = ResourceFile.builder().id(4L).author(author).filePath("a").build();
        when(resources.findById(4L)).thenReturn(Optional.of(resource));
        assertThrows(RuntimeException.class, () -> service.deleteResource(4L, 2L));
        service.deleteResource(4L, 1L);
        verify(storage).deleteFile("a");
        verify(resources).delete(resource);

        when(resources.findAllByOrderByUploadDateDesc()).thenReturn(List.of(resource));
        assertNotNull(service.getAllResources(1L).get(0).getDate());
        when(resources.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.getResourceEntity(99L));
    }
}
