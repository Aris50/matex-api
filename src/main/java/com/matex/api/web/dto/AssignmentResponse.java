package com.matex.api.web.dto;

import java.time.Instant;

public record AssignmentResponse(
        Long id,
        Long homeworkId,
        Long studentId,
        String status,
        Instant assignedAt
) {}