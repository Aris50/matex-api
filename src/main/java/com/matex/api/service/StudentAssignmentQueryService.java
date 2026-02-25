package com.matex.api.service;

import com.matex.api.domain.StudentAssignment;
import com.matex.api.domain.enums.UserRole;
import com.matex.api.repo.StudentAssignmentRepository;
import com.matex.api.repo.UserRepository;
import com.matex.api.web.dto.StudentAssignmentListItemResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class StudentAssignmentQueryService {

    private final StudentAssignmentRepository studentAssignmentRepository;
    private final UserRepository userRepository;

    public StudentAssignmentQueryService(StudentAssignmentRepository studentAssignmentRepository,
                                         UserRepository userRepository) {
        this.studentAssignmentRepository = studentAssignmentRepository;
        this.userRepository = userRepository;
    }

    public List<StudentAssignmentListItemResponse> listForStudent(Long studentId) {
        if (studentId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "studentId is required");
        }

        var student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "student not found"));

        if (student.getRole() != UserRole.STUDENT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user is not a student");
        }

        List<StudentAssignment> assignments = studentAssignmentRepository.findByStudentIdOrderByAssignedAtDesc(studentId);

        return assignments.stream()
                .map(sa -> new StudentAssignmentListItemResponse(
                        sa.getId(),
                        sa.getHomework().getId(),
                        sa.getHomework().getTitle(),
                        sa.getHomework().getDueAt(),
                        sa.getStatus().name(),
                        sa.getAssignedAt()
                ))
                .toList();
    }
}