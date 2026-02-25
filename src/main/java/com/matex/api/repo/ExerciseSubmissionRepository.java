package com.matex.api.repo;

import com.matex.api.domain.ExerciseSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExerciseSubmissionRepository extends JpaRepository<ExerciseSubmission, Long> {
    List<ExerciseSubmission> findByStudentAssignmentIdAndExerciseIdOrderByAttemptNoDesc(Long studentAssignmentId, Long exerciseId);
    Optional<ExerciseSubmission> findTopByStudentAssignmentIdAndExerciseIdOrderByAttemptNoDesc(Long studentAssignmentId, Long exerciseId);
}