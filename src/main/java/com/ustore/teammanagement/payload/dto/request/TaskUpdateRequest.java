package com.ustore.teammanagement.payload.dto.request;

import com.ustore.teammanagement.core.entity.Task;
import com.ustore.teammanagement.core.enums.Priority;
import com.ustore.teammanagement.core.enums.TaskStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record TaskUpdateRequest(
        @Size(min = 2, max = 200, message = "Título deve ter entre 2 e 200 caracteres")
        String title,

        @Size(max = 4000, message = "Descrição deve ter no máximo 4000 caracteres")
        String description,

        TaskStatus status,

        Priority priority,

        @FutureOrPresent(message = "Data de vencimento deve ser hoje ou no futuro")
        LocalDate dueDate,

        String project,

        List<String> tags,

        UUID assigneeId
) {
    public void updateTask(Task task, TaskUpdateRequest taskUpdateRequest) {
        if (taskUpdateRequest.title() != null) {
            task.setTitle(taskUpdateRequest.title());
        }
        if (taskUpdateRequest.description() != null) {
            task.setDescription(taskUpdateRequest.description());
        }
        if (taskUpdateRequest.status() != null) {
            task.setStatus(taskUpdateRequest.status());
        }
        if (taskUpdateRequest.priority() != null) {
            task.setPriority(taskUpdateRequest.priority());
        }
        if (taskUpdateRequest.dueDate() != null) {
            task.setDueDate(taskUpdateRequest.dueDate());
        }
        if (taskUpdateRequest.project() != null) {
            task.setProject(taskUpdateRequest.project());
        }
        if (taskUpdateRequest.tags() != null) {
            task.setTags(taskUpdateRequest.tags());
        }
        if (taskUpdateRequest.assigneeId() != null) {
            task.getAssignee().setId(taskUpdateRequest.assigneeId());
        }
    }
}
