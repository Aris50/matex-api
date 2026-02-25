package com.matex.api.web.dto;

public record SubmissionFileResponse(
        Long fileId,
        String originalFilename,
        String contentType,
        Long sizeBytes
) {}