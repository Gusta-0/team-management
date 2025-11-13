package com.ustore.teammanagement.payload.dto.request;


import com.ustore.teammanagement.core.entity.Task;
import com.ustore.teammanagement.core.enums.Priority;
import com.ustore.teammanagement.core.enums.TaskStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record TaskRequest (
        @NotBlank(message = "Título é obrigatório")
        @Size(min = 2, max = 200, message = "Título deve ter entre 2 e 200 caracteres")
        String title,

        @NotBlank(message = "Descrição é obrigatória")
        @Size(max = 4000, message = "Descrição deve ter no máximo 4000 caracteres")
        String description,

        @NotNull(message = "Status é obrigatório")
        TaskStatus status,

        @NotNull(message = "Prioridade é obrigatória")
        Priority priority,

        @FutureOrPresent(message = "Data de vencimento deve ser hoje ou no futuro")
        LocalDate dueDate,

        @NotBlank(message = "Projeto é obrigatório")
        String project,

        List<String> tags,

        @NotNull(message = "Responsável é obrigatório")
        UUID assigneeId
){

    public TaskRequest (Task task){
        this(
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                task.getProject(),
                task.getTags(),
                task.getAssignee() != null ? task.getAssignee().getId() : null
        );
    }

    public Task toTask() {
        return Task.builder()
                .title(this.title)
                .description(this.description)
                .status(this.status)
                .priority(this.priority)
                .dueDate(this.dueDate)
                .project(this.project)
                .tags(this.tags)
                .build();
    }
}
