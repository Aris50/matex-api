package com.matex.api.web.dto;

import java.time.Instant;

public record ExerciseWithLatestSubmissionResponse(
        Long exerciseId,
        Integer orderIndex,
        String instructionText,
        Instant createdAt,
        LatestSubmissionResponse latestSubmission
) {}