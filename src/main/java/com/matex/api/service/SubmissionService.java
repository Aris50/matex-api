package com.matex.api.service;

import com.matex.api.domain.*;
import com.matex.api.domain.enums.UserRole;
import com.matex.api.repo.*;
import com.matex.api.web.dto.SubmissionCreatedResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class SubmissionService {

    private final UserRepository userRepository;
    private final StudentAssignmentRepository assignmentRepository;
    private final ExerciseRepository exerciseRepository;
    private final ExerciseSubmissionRepository submissionRepository;
    private final StoredFileRepository fileRepository;
    private final SubmissionFileRepository submissionFileRepository;
    private final FileStorageService storageService;

    public SubmissionService(UserRepository userRepository,
                             StudentAssignmentRepository assignmentRepository,
                             ExerciseRepository exerciseRepository,
                             ExerciseSubmissionRepository submissionRepository,
                             StoredFileRepository fileRepository,
                             SubmissionFileRepository submissionFileRepository,
                             FileStorageService storageService) {
        this.userRepository = userRepository;
        this.assignmentRepository = assignmentRepository;
        this.exerciseRepository = exerciseRepository;
        this.submissionRepository = submissionRepository;
        this.fileRepository = fileRepository;
        this.submissionFileRepository = submissionFileRepository;
        this.storageService = storageService;
    }

    @Transactional
    public SubmissionCreatedResponse submit(Long studentId,
                                            Long assignmentId,
                                            Long exerciseId,
                                            List<MultipartFile> files,
                                            String textResult) {
        if (studentId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "studentId is required");
        if (assignmentId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "assignmentId is required");
        if (exerciseId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "exerciseId is required");
        if (files == null || files.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "files is required");

        var student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "student not found"));
        if (student.getRole() != UserRole.STUDENT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user is not a student");
        }

        StudentAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "assignment not found"));

        if (!assignment.getStudent().getId().equals(studentId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "not your assignment");
        }

        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "exercise not found"));

        if (!exercise.getHomework().getId().equals(assignment.getHomework().getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "exercise does not belong to this homework");
        }

        int nextAttempt = submissionRepository
                .findTopByStudentAssignmentIdAndExerciseIdOrderByAttemptNoDesc(assignmentId, exerciseId)
                .map(s -> s.getAttemptNo() + 1)
                .orElse(1);

        ExerciseSubmission submission = new ExerciseSubmission();
        submission.setStudentAssignment(assignment);
        submission.setExercise(exercise);
        submission.setAttemptNo(nextAttempt);
        submission.setTextResult(textResult);

        submission = submissionRepository.save(submission);

        List<Long> fileIds = new ArrayList<>();
        int orderIndex = 1;

        for (MultipartFile mf : files) {
            var stored = storageService.store(mf);

            StoredFile f = new StoredFile();
            f.setStorageKey(stored.storageKey());
            f.setOriginalFilename(stored.originalFilename());
            f.setContentType(stored.contentType());
            f.setSizeBytes(stored.sizeBytes());
            f = fileRepository.save(f);

            SubmissionFile sf = new SubmissionFile();
            sf.setSubmission(submission);
            sf.setFile(f);
            sf.setOrderIndex(orderIndex++);
            submissionFileRepository.save(sf);

            fileIds.add(f.getId());
        }

        return new SubmissionCreatedResponse(
                submission.getId(),
                submission.getAttemptNo(),
                submission.getSubmittedAt(),
                submission.getTextResult(),
                fileIds
        );
    }
}