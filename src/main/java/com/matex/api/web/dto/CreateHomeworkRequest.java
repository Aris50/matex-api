package com.matex.api.web.dto;

import java.time.LocalDateTime;

public record CreateHomeworkRequest(
        String title,
        String description,
        LocalDateTime dueAt
) {}