package com.matex.api.web.dto;

import java.time.Instant;
import java.time.LocalDateTime;

public record StudentAssignmentListItemResponse(
        Long assignmentId,
        Long homeworkId,
        String homeworkTitle,
        LocalDateTime dueAt,
        String status,
        Instant assignedAt
) {}