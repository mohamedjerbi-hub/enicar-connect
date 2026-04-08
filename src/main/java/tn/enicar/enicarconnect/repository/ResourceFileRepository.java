package tn.enicar.enicarconnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.enicar.enicarconnect.model.ResourceFile;

import java.util.List;

@Repository
public interface ResourceFileRepository extends JpaRepository<ResourceFile, Long> {
    List<ResourceFile> findAllByOrderByUploadDateDesc();
}
