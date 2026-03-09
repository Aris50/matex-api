package com.matex.api.web.dto;

public record CreateAccountRequest(
        String email,
        String password,
        String fullName,
        String role  // STUDENT or TEACHER
) {}

