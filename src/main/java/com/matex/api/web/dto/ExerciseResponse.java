package com.matex.api.web.dto;

import java.time.Instant;

public record ExerciseResponse(
        Long id,
        Long homeworkId,
        Integer orderIndex,
        String instructionText,
        Instant createdAt,
        String imagePath,
        String imageOriginalName,
        String imageContentType,
        Long imageSizeBytes
) {}