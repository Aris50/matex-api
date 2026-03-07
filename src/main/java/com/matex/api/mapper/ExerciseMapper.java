package com.matex.api.mapper;

import com.matex.api.domain.Exercise;
import com.matex.api.domain.Homework;
import com.matex.api.web.dto.CreateExerciseRequest;
import com.matex.api.web.dto.ExerciseResponse;

public class ExerciseMapper {

    public Exercise toEntity(CreateExerciseRequest req, Homework homework) {
        Exercise ex = new Exercise();
        ex.setHomework(homework);
        ex.setOrderIndex(req.orderIndex());
        ex.setInstructionText(req.instructionText());
        return ex;
    }

    public ExerciseResponse toResponse(Exercise ex) {
        return new ExerciseResponse(
                ex.getId(),
                ex.getHomework().getId(),
                ex.getOrderIndex(),
                ex.getInstructionText(),
                ex.getCreatedAt(),
                ex.getImagePath(),
                ex.getImageOriginalName(),
                ex.getImageContentType(),
                ex.getImageSizeBytes()
        );
    }
}