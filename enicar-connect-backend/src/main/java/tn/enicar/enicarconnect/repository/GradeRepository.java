package tn.enicar.enicarconnect.repository;

import tn.enicar.enicarconnect.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findByStudentEmail(String studentEmail);
    List<Grade> findBySemester(String semester);
}
