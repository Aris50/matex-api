package com.matex.api.web.dto;

import java.time.Instant;
import java.util.List;

public record SubmissionCreatedResponse(
        Long submissionId,
        Integer attemptNo,
        Instant submittedAt,
        String textResult,
        List<Long> fileIds
) {}