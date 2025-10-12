package com.ustore.teammanagement.payload.dto.request;

import com.ustore.teammanagement.enums.Priority;
import com.ustore.teammanagement.enums.TaskStatus;

import java.time.LocalDate;
import java.util.UUID;

public record TaskFilterRequest(
        String assigneeName,
        String title,
        String project,
        UUID assigneeId,
        String tag,
        String createdByName,
        TaskStatus status,
        Priority priority,
        LocalDate dueDateFrom,
        LocalDate dueDateTo,
        Boolean onlyOverdue
) {
}
