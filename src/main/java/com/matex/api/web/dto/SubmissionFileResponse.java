package com.matex.api.web.dto;

public record SubmissionFileResponse(
        Long fileId,
        String storageKey,
        String originalFilename,
        String contentType,
        Long sizeBytes
) {}