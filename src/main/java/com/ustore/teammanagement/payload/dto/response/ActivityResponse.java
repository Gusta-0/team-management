package com.ustore.teammanagement.payload.dto.response;

import java.time.OffsetDateTime;

public record ActivityResponse(
        String authorName,
        String action,
        String taskTitle,
        OffsetDateTime timestamp
) {}
