package com.ustore.teammanagement.config;

import com.ustore.teammanagement.core.enums.Priority;
import com.ustore.teammanagement.core.enums.TaskStatus;
import com.ustore.teammanagement.payload.dto.request.TaskRequest;
import com.ustore.teammanagement.payload.dto.request.TaskUpdateRequest;
import com.ustore.teammanagement.payload.dto.response.TaskResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@Tag(name = "Tasks", description = "Endpoints for managing tasks")
public interface TaskAPI {

    @Operation(
            summary = "Create a new task",
            description = "Allows ADMIN and MANAGER users to create new tasks.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Task successfully created",
                            content = @Content(schema = @Schema(implementation = TaskResponse.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "404", description = "User or assignee not found")
            }
    )
    @PostMapping
    ResponseEntity<TaskResponse> createTask(TaskRequest request);

    @Operation(
            summary = "Filter tasks",
            description = "Filters tasks by title, project, priority, status, assignee, due date, etc.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Filtered list of tasks",
                            content = @Content(schema = @Schema(implementation = Page.class)))
            }
    )
    @GetMapping
    public ResponseEntity<Page<TaskResponse>> filterTasks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String project,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) String assigneeName,
            @RequestParam(required = false) String createdByName,
            @RequestParam(required = false) LocalDate dueDateFrom,
            @RequestParam(required = false) LocalDate dueDateTo,
            @RequestParam(required = false) Boolean onlyOverdue,
            @ParameterObject Pageable pageable
    );


    @Operation(
            summary = "Update an existing task",
            description = "Allows any authenticated user to update a task.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Task successfully updated",
                            content = @Content(schema = @Schema(implementation = TaskResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Task not found"
                    )
            }
    )
    @PutMapping("/{id}")
    ResponseEntity<TaskResponse> updateTask(
            @Parameter(description = "Task ID", required = true)
            UUID id,
            @Parameter(description = "Task data to update", required = true)
            TaskUpdateRequest request
    );


    @Operation(
            summary = "Delete a task",
            description = "Allows ADMIN and MANAGER users to delete a task.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Task successfully deleted"),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "404", description = "Task not found")
            }
    )
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteTask(@Parameter(description = "Task ID") UUID id);
}
