package com.matex.api.controller.teacher;

import com.matex.api.domain.Exercise;
import com.matex.api.domain.ExerciseSubmission;
import com.matex.api.domain.StudentAssignment;
import com.matex.api.domain.User;
import com.matex.api.domain.enums.UserRole;
import com.matex.api.repo.ExerciseRepository;
import com.matex.api.repo.ExerciseSubmissionRepository;
import com.matex.api.repo.StudentAssignmentRepository;
import com.matex.api.repo.UserRepository;
import com.matex.api.service.StudentAssignmentDetailsService;
import com.matex.api.web.dto.StudentAssignmentDetailsResponse;
import com.matex.api.web.dto.TeacherStudentAssignmentResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/teacher/students")
public class TeacherStudentController {

    private final UserRepository userRepository;
    private final StudentAssignmentRepository assignmentRepository;
    private final ExerciseSubmissionRepository submissionRepository;
    private final ExerciseRepository exerciseRepository;
    private final StudentAssignmentDetailsService detailsService;

    public TeacherStudentController(UserRepository userRepository,
                                     StudentAssignmentRepository assignmentRepository,
                                     ExerciseSubmissionRepository submissionRepository,
                                     ExerciseRepository exerciseRepository,
                                     StudentAssignmentDetailsService detailsService) {
        this.userRepository = userRepository;
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.exerciseRepository = exerciseRepository;
        this.detailsService = detailsService;
    }

    @GetMapping
    public List<Map<String, Object>> getAllStudents() {
        return userRepository.findByRole(UserRole.STUDENT)
                .stream()
                .map(u -> Map.<String, Object>of(
                        "id", u.getId(),
                        "email", u.getEmail(),
                        "fullName", u.getFullName()
                ))
                .toList();
    }

    @GetMapping("/{studentId}/assignments")
    public List<TeacherStudentAssignmentResponse> getStudentAssignments(@PathVariable Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));

        if (student.getRole() != UserRole.STUDENT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not a student");
        }

        List<StudentAssignment> assignments = assignmentRepository.findByStudentIdOrderByAssignedAtDesc(studentId);

        return assignments.stream().map(sa -> {
            List<ExerciseSubmission> submissions = submissionRepository.findByStudentAssignmentId(sa.getId());
            List<Exercise> exercises = exerciseRepository.findByHomeworkIdOrderByOrderIndexAsc(sa.getHomework().getId());

            int totalExercises = exercises.size();
            Set<Long> exercisesWithSubs = submissions.stream()
                    .map(s -> s.getExercise().getId())
                    .collect(Collectors.toSet());
            int submittedCount = exercisesWithSubs.size();

            String deliveryStatus;
            if (submittedCount == 0) {
                deliveryStatus = "UNDELIVERED";
            } else if (submittedCount < totalExercises) {
                deliveryStatus = "PARTLY_DELIVERED";
            } else {
                LocalDateTime dueAt = sa.getHomework().getDueAt();
                if (dueAt != null) {
                    Instant dueInstant = dueAt.atZone(ZoneId.systemDefault()).toInstant();
                    boolean anyLate = submissions.stream()
                            .anyMatch(s -> s.getSubmittedAt().isAfter(dueInstant));
                    deliveryStatus = anyLate ? "DELIVERED_LATE" : "DELIVERED";
                } else {
                    deliveryStatus = "DELIVERED";
                }
            }

            return new TeacherStudentAssignmentResponse(
                    sa.getId(),
                    sa.getHomework().getId(),
                    sa.getHomework().getTitle(),
                    sa.getHomework().getDescription(),
                    sa.getHomework().getDueAt(),
                    sa.getAssignedAt(),
                    deliveryStatus
            );
        }).toList();
    }

    @GetMapping("/{studentId}/assignments/{assignmentId}")
    public StudentAssignmentDetailsResponse getAssignmentDetails(
            @PathVariable Long studentId,
            @PathVariable Long assignmentId) {

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        if (student.getRole() != UserRole.STUDENT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not a student");
        }

        return detailsService.getDetailsForTeacher(assignmentId);
    }
}
