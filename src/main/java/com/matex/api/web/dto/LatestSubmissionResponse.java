package com.matex.api.web.dto;

import java.time.Instant;
import java.util.List;

public record LatestSubmissionResponse(
        Long submissionId,
        Integer attemptNo,
        String textResult,
        Instant submittedAt,
        List<SubmissionFileResponse> files
) {}