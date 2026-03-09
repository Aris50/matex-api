package com.matex.api.web.dto;

public record LoginResponse(
        String token,
        Long userId,
        String email,
        String role,
        String fullName
) {}

