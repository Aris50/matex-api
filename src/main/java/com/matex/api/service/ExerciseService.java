package com.matex.api.service;

import com.matex.api.domain.Exercise;
import com.matex.api.domain.Homework;
import com.matex.api.repo.ExerciseRepository;
import com.matex.api.repo.HomeworkRepository;
import com.matex.api.web.dto.CreateExerciseRequest;
import com.matex.api.mapper.ExerciseMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ExerciseService {

    private final HomeworkRepository homeworkRepository;
    private final ExerciseRepository exerciseRepository;
    private final ExerciseMapper exerciseMapper = new ExerciseMapper();

    public ExerciseService(HomeworkRepository homeworkRepository, ExerciseRepository exerciseRepository) {
        this.homeworkRepository = homeworkRepository;
        this.exerciseRepository = exerciseRepository;
    }

    public Exercise addExercise(Long homeworkId, CreateExerciseRequest req) {
        if (homeworkId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "homeworkId is required");
        }
        if (req.orderIndex() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "orderIndex is required");
        }
        if (req.instructionText() == null || req.instructionText().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "instructionText is required");
        }

        Homework hw = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "homework not found"));

        Exercise ex = exerciseMapper.toEntity(req, hw);
        return exerciseRepository.save(ex);
    }
}