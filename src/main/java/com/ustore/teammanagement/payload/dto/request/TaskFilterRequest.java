package com.ustore.teammanagement.payload.dto.request;

import com.ustore.teammanagement.core.enums.Priority;
import com.ustore.teammanagement.core.enums.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.UUID;

public record TaskFilterRequest(
        @Schema(description = "Nome do responsável") String assigneeName,

        @Schema(description = "Título da tarefa") String title,

        @Schema(description = "Nome do projeto") String project,

        @Schema(description = "ID do responsável") UUID assigneeId,

        @Schema(description = "Nome do criador") String createdByName,

        @Schema(description = "Status da tarefa", allowableValues = {"TODO", "IN_PROGRESS", "DONE"})
        TaskStatus status,

        @Schema(description = "Prioridade", allowableValues = {"LOW", "MEDIUM", "HIGH"})
        Priority priority,

        @Schema(description = "Data inicial do vencimento (yyyy-MM-dd)")
        LocalDate dueDateFrom,

        @Schema(description = "Data final do vencimento (yyyy-MM-dd)")
        LocalDate dueDateTo,

        @Schema(description = "Filtrar apenas tarefas atrasadas")
        Boolean onlyOverdue
) {
}
