package tn.enicar.enicarconnect.repository;

import tn.enicar.enicarconnect.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByStudentEmail(String studentEmail);
    List<Attendance> findByCourseCode(String courseCode);
}
