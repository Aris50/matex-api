package com.matex.api.web.dto;

import java.time.Instant;

public record AccountResponse(
        Long id,
        String email,
        String fullName,
        String role,
        Instant createdAt
) {}

