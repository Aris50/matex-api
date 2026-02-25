package com.matex.api.web.dto;

public record CreateExerciseRequest(
        Integer orderIndex,
        String instructionText
) {}