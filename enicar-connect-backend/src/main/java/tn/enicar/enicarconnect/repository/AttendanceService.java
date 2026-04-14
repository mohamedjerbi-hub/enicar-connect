package tn.enicar.enicarconnect.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.enicar.enicarconnect.model.Attendance;
import tn.enicar.enicarconnect.repository.AttendanceRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    public Attendance recordAttendance(String studentEmail, String courseCode) {
        Attendance attendance = Attendance.builder()
                .studentEmail(studentEmail)
                .courseCode(courseCode)
                .present(true)
                .build();
        return attendanceRepository.save(attendance);
    }

    public List<Attendance> getByStudent(String studentEmail) {
        return attendanceRepository.findByStudentEmail(studentEmail);
    }

    public List<Attendance> getByCourse(String courseCode) {
        return attendanceRepository.findByCourseCode(courseCode);
    }
}
