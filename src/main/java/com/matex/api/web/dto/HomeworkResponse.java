package com.matex.api.web.dto;

import java.time.Instant;
import java.time.LocalDateTime;

public record HomeworkResponse(
        Long id,
        Long teacherId,
        String title,
        String description,
        LocalDateTime dueAt,
        Instant createdAt
) {}