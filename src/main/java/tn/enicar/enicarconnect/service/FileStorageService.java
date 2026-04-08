package tn.enicar.enicarconnect.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    // Dossier 'uploads' à la racine du projet backend
    private final Path fileStorageLocation;

    public FileStorageService() {
        this.fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Impossible de créer le répertoire où les fichiers seront stockés.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        String originalFileName = StringUtils
                .cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "file");
        try {
            if (originalFileName.contains("..")) {
                throw new RuntimeException("Fichier invalide " + originalFileName);
            }

            // Génère un nom unique pour éviter les collisions (UUID)
            String fileExtension = "";
            if (originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String targetFileName = UUID.randomUUID().toString() + fileExtension;

            Path targetLocation = this.fileStorageLocation.resolve(targetFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return targetFileName;
        } catch (IOException ex) {
            throw new RuntimeException("Erreur de stockage du fichier " + originalFileName, ex);
        }
    }

    public Path getFilePath(String fileName) {
        return this.fileStorageLocation.resolve(fileName).normalize();
    }

    public void deleteFile(String fileName) {
        try {
            Path file = this.fileStorageLocation.resolve(fileName).normalize();
            Files.deleteIfExists(file);
        } catch (IOException ex) {
            System.err.println("Impossible de supprimer le fichier: " + fileName);
        }
    }
}
