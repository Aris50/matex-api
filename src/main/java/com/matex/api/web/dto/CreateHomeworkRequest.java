package com.matex.api.web.dto;

import java.time.LocalDateTime;

public record CreateHomeworkRequest(
        Long teacherId,
        String title,
        String description,
        LocalDateTime dueAt
) {}