package com.matex.api.repo;

import com.matex.api.domain.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    List<Exercise> findByHomeworkIdOrderByOrderIndexAsc(Long homeworkId);
}