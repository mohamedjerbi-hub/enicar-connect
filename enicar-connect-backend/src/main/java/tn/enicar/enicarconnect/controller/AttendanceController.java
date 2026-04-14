package tn.enicar.enicarconnect.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.enicar.enicarconnect.model.Attendance;
import tn.enicar.enicarconnect.service.AttendanceService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/scan")
    public ResponseEntity<Attendance> scan(@RequestBody Map<String, String> payload) {
        Attendance saved = attendanceService.recordAttendance(
                payload.get("studentEmail"),
                payload.get("courseCode")
        );
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/student/{email}")
    public ResponseEntity<List<Attendance>> byStudent(@PathVariable String email) {
        return ResponseEntity.ok(attendanceService.getByStudent(email));
    }

    @GetMapping("/course/{code}")
    public ResponseEntity<List<Attendance>> byCourse(@PathVariable String code) {
        return ResponseEntity.ok(attendanceService.getByCourse(code));
    }
}
