package tn.enicar.enicarconnect.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.enicar.enicarconnect.model.Grade;
import tn.enicar.enicarconnect.repository.GradeRepository;

import java.util.List;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
public class GradeController {

    private final GradeRepository gradeRepository;

    @GetMapping("/student/{email}")
    public ResponseEntity<List<Grade>> getByStudent(@PathVariable String email) {
        return ResponseEntity.ok(gradeRepository.findByStudentEmail(email));
    }

    @PostMapping
    public ResponseEntity<Grade> addGrade(@RequestBody Grade grade) {
        return ResponseEntity.ok(gradeRepository.save(grade));
    }
}
