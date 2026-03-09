package com.matex.api.repo;

import com.matex.api.domain.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    List<Exercise> findByHomeworkIdOrderByOrderIndexAsc(Long homeworkId);
    List<Exercise> findByHomeworkId(Long homeworkId);

    @Query("SELECT COALESCE(MAX(e.orderIndex), 0) FROM Exercise e WHERE e.homework.id = :homeworkId")
    int findMaxOrderIndexByHomeworkId(Long homeworkId);
}