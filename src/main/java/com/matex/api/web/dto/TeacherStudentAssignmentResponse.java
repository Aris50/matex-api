package com.matex.api.web.dto;

import java.time.Instant;
import java.time.LocalDateTime;

public record TeacherStudentAssignmentResponse(
        Long assignmentId,
        Long homeworkId,
        String homeworkTitle,
        String homeworkDescription,
        LocalDateTime dueAt,
        Instant assignedAt,
        String deliveryStatus  // DELIVERED, DELIVERED_LATE, UNDELIVERED
) {}

