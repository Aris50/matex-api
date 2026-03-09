package com.matex.api.web.dto;

public record MeResponse(
        Long id,
        String email,
        String role,
        String fullName
) {}

