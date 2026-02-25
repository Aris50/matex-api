package com.matex.api.controller.teacher;

import com.matex.api.domain.Exercise;
import com.matex.api.service.ExerciseService;
import com.matex.api.web.dto.CreateExerciseRequest;
import com.matex.api.web.dto.ExerciseResponse;
import com.matex.api.mapper.ExerciseMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/teacher/homeworks/{homeworkId}/exercises")
public class TeacherExerciseController {

    private final ExerciseService exerciseService;
    private final ExerciseMapper exerciseMapper = new ExerciseMapper();

    public TeacherExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExerciseResponse add(@PathVariable Long homeworkId, @RequestBody CreateExerciseRequest req) {
        Exercise ex = exerciseService.addExercise(homeworkId, req);
        return exerciseMapper.toResponse(ex);
    }
}