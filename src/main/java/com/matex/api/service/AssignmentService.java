package com.matex.api.service;

import com.matex.api.domain.Homework;
import com.matex.api.domain.StudentAssignment;
import com.matex.api.domain.User;
import com.matex.api.domain.enums.AssignmentStatus;
import com.matex.api.domain.enums.UserRole;
import com.matex.api.repo.HomeworkRepository;
import com.matex.api.repo.StudentAssignmentRepository;
import com.matex.api.repo.UserRepository;
import com.matex.api.web.dto.CreateAssignmentRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class AssignmentService {

    private final HomeworkRepository homeworkRepository;
    private final UserRepository userRepository;
    private final StudentAssignmentRepository studentAssignmentRepository;

    public AssignmentService(HomeworkRepository homeworkRepository,
                             UserRepository userRepository,
                             StudentAssignmentRepository studentAssignmentRepository) {
        this.homeworkRepository = homeworkRepository;
        this.userRepository = userRepository;
        this.studentAssignmentRepository = studentAssignmentRepository;
    }

    public List<StudentAssignment> assignHomework(Long homeworkId, CreateAssignmentRequest req) {
        if (homeworkId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "homeworkId is required");
        }
        if (req.studentIds() == null || req.studentIds().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "studentIds is required");
        }

        Homework hw = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "homework not found"));

        List<StudentAssignment> created = new ArrayList<>();

        for (Long studentId : req.studentIds()) {
            User student = userRepository.findById(studentId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "student not found: " + studentId));

            if (student.getRole() != UserRole.STUDENT) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user is not a student: " + studentId);
            }

            StudentAssignment sa = new StudentAssignment();
            sa.setHomework(hw);
            sa.setStudent(student);
            sa.setStatus(AssignmentStatus.ASSIGNED);

            created.add(studentAssignmentRepository.save(sa));
        }

        return created;
    }
}