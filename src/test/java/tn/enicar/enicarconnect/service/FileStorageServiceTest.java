package tn.enicar.enicarconnect.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileStorageServiceTest {

    private final FileStorageService service = new FileStorageService();

    @AfterEach
    void cleanUploads() throws Exception {
        try (var files = Files.list(Path.of("uploads"))) {
            files.filter(Files::isRegularFile).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (Exception ignored) {
                }
            });
        }
    }

    @Test
    void storesAndDeletesFileWithExtension() throws Exception {
        var file = new MockMultipartFile("file", "notes.pdf", "application/pdf", "content".getBytes());

        String storedName = service.storeFile(file);

        assertThat(storedName).endsWith(".pdf");
        assertThat(Files.exists(service.getFilePath(storedName))).isTrue();
        service.deleteFile(storedName);
        assertThat(Files.exists(service.getFilePath(storedName))).isFalse();
    }

    @Test
    void rejectsPathTraversal() {
        var file = new MockMultipartFile("file", "../secret.txt", "text/plain", "content".getBytes());

        assertThatThrownBy(() -> service.storeFile(file))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Fichier invalide");
    }

    @Test
    void storesFileWithoutExtensionAndIgnoresMissingFileOnDelete() {
        var file = new MockMultipartFile("file", null, "text/plain", "content".getBytes());

        String storedName = service.storeFile(file);

        assertThat(storedName).doesNotContain(".");
        service.deleteFile("missing-file");
    }
}
