package com.matex.api.web.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public record StudentAssignmentDetailsResponse(
        Long assignmentId,
        String status,
        Instant assignedAt,
        Long homeworkId,
        String homeworkTitle,
        String homeworkDescription,
        LocalDateTime dueAt,
        List<ExerciseWithLatestSubmissionResponse> exercises
) {}