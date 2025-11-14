package com.ustore.teammanagement.payload.dto.response;

import com.ustore.teammanagement.core.enums.Priority;
import com.ustore.teammanagement.core.enums.TaskStatus;

import java.time.LocalDate;
import java.util.UUID;

public record ProjectProgressResponse(
        UUID id,
        String name,
        String description,
        double progress,
        long totalTasks,
        long completedTasks,
        long teamMembers,
        LocalDate dueDate,
        TaskStatus status,
        Priority priority
) {
}
