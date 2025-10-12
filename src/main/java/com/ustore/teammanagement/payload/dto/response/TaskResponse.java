package com.ustore.teammanagement.payload.dto.response;

import com.ustore.teammanagement.core.entity.Task;
import com.ustore.teammanagement.enums.Priority;
import com.ustore.teammanagement.enums.TaskStatus;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record TaskResponse (
        UUID id,
        String title,
        String description,
        TaskStatus status,
        Priority priority,
        LocalDate dueDate,
        String project,
        List<String> tags,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        MemberInfo assignee,
        MemberInfo createdBy
) {
    public TaskResponse (Task task) {
        this(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                task.getProject(),
                task.getTags(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                new MemberInfo(task.getAssignee().getName()),
                new MemberInfo(task.getCreatedBy().getName())
        );
    }


}
