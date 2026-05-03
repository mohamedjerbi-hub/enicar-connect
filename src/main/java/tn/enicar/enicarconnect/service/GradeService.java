package tn.enicar.enicarconnect.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.enicar.enicarconnect.model.Grade;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.repository.GradeRepository;
import tn.enicar.enicarconnect.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GradeService {

    private final GradeRepository gradeRepository;
    private final UserRepository userRepository;

    public List<Grade> getGradesForStudent(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        return gradeRepository.findByStudent(student);
    }

    public void submitGradeReclamation(Long studentId, Long gradeId, String reason) {
        // Implementation logic for reclamation workflow goes here
        System.out.println("Reclamation submitted for grade ID: " + gradeId + " by student ID: " + studentId);
    }
}
