package com.matex.api.web.dto;

import java.util.List;

public record CreateAssignmentRequest(
        List<Long> studentIds
) {}