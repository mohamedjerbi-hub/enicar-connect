package tn.enicar.enicarconnect.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.enicar.enicarconnect.model.Grade;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.repository.GradeRepository;
import tn.enicar.enicarconnect.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GradeServiceTest {
    @Mock GradeRepository grades;
    @Mock UserRepository users;
    @InjectMocks GradeService service;

    @Test
    void readsStudentGradesAndHandlesMissingStudent() {
        User student = new User();
        student.setId(1L);
        Grade grade = Grade.builder().student(student).subject("Java").value(15.0).semester(1).build();
        when(users.findById(1L)).thenReturn(Optional.of(student));
        when(grades.findByStudent(student)).thenReturn(List.of(grade));
        assertEquals(1, service.getGradesForStudent(1L).size());
        service.submitGradeReclamation(1L, 2L, "Review");
        when(users.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.getGradesForStudent(99L));
    }
}
