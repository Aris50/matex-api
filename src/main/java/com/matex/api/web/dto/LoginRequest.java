package com.matex.api.web.dto;

public record LoginRequest(
        String email,
        String password
) {}

