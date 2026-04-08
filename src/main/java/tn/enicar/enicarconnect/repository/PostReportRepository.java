package tn.enicar.enicarconnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.enicar.enicarconnect.model.PostReport;
import tn.enicar.enicarconnect.model.ReportStatus;

import java.util.List;

public interface PostReportRepository extends JpaRepository<PostReport, Long> {

    List<PostReport> findByStatus(ReportStatus status);

    int countByPostId(Long postId);
}
