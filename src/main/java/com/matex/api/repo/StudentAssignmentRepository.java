package com.matex.api.repo;

import com.matex.api.domain.StudentAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentAssignmentRepository extends JpaRepository<StudentAssignment, Long> {
    Optional<StudentAssignment> findByHomeworkIdAndStudentId(Long homeworkId, Long studentId);
    List<StudentAssignment> findByStudentIdOrderByAssignedAtDesc(Long studentId);
}