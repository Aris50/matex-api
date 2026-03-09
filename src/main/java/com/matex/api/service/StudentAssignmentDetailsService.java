package com.matex.api.service;

import com.matex.api.domain.Exercise;
import com.matex.api.domain.ExerciseSubmission;
import com.matex.api.domain.SubmissionFile;
import com.matex.api.domain.enums.UserRole;
import com.matex.api.repo.ExerciseRepository;
import com.matex.api.repo.ExerciseSubmissionRepository;
import com.matex.api.repo.StudentAssignmentRepository;
import com.matex.api.repo.SubmissionFileRepository;
import com.matex.api.repo.UserRepository;
import com.matex.api.web.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class StudentAssignmentDetailsService {

    private final StudentAssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;
    private final ExerciseSubmissionRepository submissionRepository;
    private final SubmissionFileRepository submissionFileRepository;

    public StudentAssignmentDetailsService(StudentAssignmentRepository assignmentRepository,
                                           UserRepository userRepository,
                                           ExerciseRepository exerciseRepository,
                                           ExerciseSubmissionRepository submissionRepository,
                                           SubmissionFileRepository submissionFileRepository) {
        this.assignmentRepository = assignmentRepository;
        this.userRepository = userRepository;
        this.exerciseRepository = exerciseRepository;
        this.submissionRepository = submissionRepository;
        this.submissionFileRepository = submissionFileRepository;
    }

    public StudentAssignmentDetailsResponse getDetails(Long studentId, Long assignmentId) {
        if (studentId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "studentId is required");
        if (assignmentId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "assignmentId is required");

        var student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "student not found"));
        if (student.getRole() != UserRole.STUDENT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user is not a student");
        }

        var assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "assignment not found"));

        if (!assignment.getStudent().getId().equals(studentId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "not your assignment");
        }

        var homework = assignment.getHomework();
        List<Exercise> exercises = exerciseRepository.findByHomeworkIdOrderByOrderIndexAsc(homework.getId());

        List<ExerciseSubmission> subs = submissionRepository.findByStudentAssignmentId(assignmentId);

        Map<Long, ExerciseSubmission> latestByExercise = new HashMap<>();
        for (ExerciseSubmission s : subs) {
            Long exId = s.getExercise().getId();
            ExerciseSubmission current = latestByExercise.get(exId);
            if (current == null || s.getAttemptNo() > current.getAttemptNo()) {
                latestByExercise.put(exId, s);
            }
        }

        List<Long> latestSubmissionIds = latestByExercise.values().stream()
                .map(ExerciseSubmission::getId)
                .toList();

        Map<Long, List<SubmissionFile>> filesBySubmissionId = new HashMap<>();
        if (!latestSubmissionIds.isEmpty()) {
            List<SubmissionFile> allFiles = submissionFileRepository.findBySubmissionIdIn(latestSubmissionIds);
            for (SubmissionFile sf : allFiles) {
                filesBySubmissionId.computeIfAbsent(sf.getSubmission().getId(), k -> new ArrayList<>()).add(sf);
            }
            for (List<SubmissionFile> list : filesBySubmissionId.values()) {
                list.sort(Comparator.comparingInt(SubmissionFile::getOrderIndex));
            }
        }

        List<ExerciseWithLatestSubmissionResponse> exerciseDtos = exercises.stream().map(ex -> {
            ExerciseSubmission latest = latestByExercise.get(ex.getId());
            LatestSubmissionResponse latestDto = null;

            if (latest != null) {
                List<SubmissionFileResponse> fileDtos = filesBySubmissionId.getOrDefault(latest.getId(), List.of())
                        .stream()
                        .map(sf -> new SubmissionFileResponse(
                                sf.getFile().getId(),
                                sf.getFile().getStorageKey(),
                                sf.getFile().getOriginalFilename(),
                                sf.getFile().getContentType(),
                                sf.getFile().getSizeBytes()
                        ))
                        .toList();

                latestDto = new LatestSubmissionResponse(
                        latest.getId(),
                        latest.getAttemptNo(),
                        latest.getTextResult(),
                        latest.getSubmittedAt(),
                        fileDtos
                );
            }

            return new ExerciseWithLatestSubmissionResponse(
                    ex.getId(),
                    ex.getOrderIndex(),
                    ex.getInstructionText(),
                    ex.getCreatedAt(),
                    ex.getImagePath(),
                    latestDto
            );
        }).toList();

        return new StudentAssignmentDetailsResponse(
                assignment.getId(),
                assignment.getStatus().name(),
                assignment.getAssignedAt(),
                homework.getId(),
                homework.getTitle(),
                homework.getDescription(),
                homework.getDueAt(),
                exerciseDtos
        );
    }

    /**
     * Teacher-facing: get full assignment details without ownership check.
     */
    public StudentAssignmentDetailsResponse getDetailsForTeacher(Long assignmentId) {
        if (assignmentId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "assignmentId is required");

        var assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "assignment not found"));

        var homework = assignment.getHomework();
        List<Exercise> exercises = exerciseRepository.findByHomeworkIdOrderByOrderIndexAsc(homework.getId());

        List<ExerciseSubmission> subs = submissionRepository.findByStudentAssignmentId(assignmentId);

        Map<Long, ExerciseSubmission> latestByExercise = new HashMap<>();
        for (ExerciseSubmission s : subs) {
            Long exId = s.getExercise().getId();
            ExerciseSubmission current = latestByExercise.get(exId);
            if (current == null || s.getAttemptNo() > current.getAttemptNo()) {
                latestByExercise.put(exId, s);
            }
        }

        List<Long> latestSubmissionIds = latestByExercise.values().stream()
                .map(ExerciseSubmission::getId)
                .toList();

        Map<Long, List<SubmissionFile>> filesBySubmissionId = new HashMap<>();
        if (!latestSubmissionIds.isEmpty()) {
            List<SubmissionFile> allFiles = submissionFileRepository.findBySubmissionIdIn(latestSubmissionIds);
            for (SubmissionFile sf : allFiles) {
                filesBySubmissionId.computeIfAbsent(sf.getSubmission().getId(), k -> new ArrayList<>()).add(sf);
            }
            for (List<SubmissionFile> list : filesBySubmissionId.values()) {
                list.sort(Comparator.comparingInt(SubmissionFile::getOrderIndex));
            }
        }

        List<ExerciseWithLatestSubmissionResponse> exerciseDtos = exercises.stream().map(ex -> {
            ExerciseSubmission latest = latestByExercise.get(ex.getId());
            LatestSubmissionResponse latestDto = null;

            if (latest != null) {
                List<SubmissionFileResponse> fileDtos = filesBySubmissionId.getOrDefault(latest.getId(), List.of())
                        .stream()
                        .map(sf -> new SubmissionFileResponse(
                                sf.getFile().getId(),
                                sf.getFile().getStorageKey(),
                                sf.getFile().getOriginalFilename(),
                                sf.getFile().getContentType(),
                                sf.getFile().getSizeBytes()
                        ))
                        .toList();

                latestDto = new LatestSubmissionResponse(
                        latest.getId(),
                        latest.getAttemptNo(),
                        latest.getTextResult(),
                        latest.getSubmittedAt(),
                        fileDtos
                );
            }

            return new ExerciseWithLatestSubmissionResponse(
                    ex.getId(),
                    ex.getOrderIndex(),
                    ex.getInstructionText(),
                    ex.getCreatedAt(),
                    ex.getImagePath(),
                    latestDto
            );
        }).toList();

        // Compute delivery status
        int totalExercises = exercises.size();
        int exercisesWithSubmissions = latestByExercise.size();
        String deliveryStatus;
        if (exercisesWithSubmissions == 0) {
            deliveryStatus = "UNDELIVERED";
        } else if (exercisesWithSubmissions < totalExercises) {
            deliveryStatus = "PARTLY_DELIVERED";
        } else {
            // All exercises have submissions — check if any were late
            if (homework.getDueAt() != null) {
                java.time.Instant dueInstant = homework.getDueAt().atZone(java.time.ZoneId.systemDefault()).toInstant();
                boolean anyLate = latestByExercise.values().stream()
                        .anyMatch(s -> s.getSubmittedAt().isAfter(dueInstant));
                deliveryStatus = anyLate ? "DELIVERED_LATE" : "DELIVERED";
            } else {
                deliveryStatus = "DELIVERED";
            }
        }

        return new StudentAssignmentDetailsResponse(
                assignment.getId(),
                deliveryStatus,
                assignment.getAssignedAt(),
                homework.getId(),
                homework.getTitle(),
                homework.getDescription(),
                homework.getDueAt(),
                exerciseDtos
        );
    }
}