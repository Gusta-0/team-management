package com.ustore.teammanagement.config;

import com.ustore.teammanagement.payload.dto.request.TaskFilterRequest;
import com.ustore.teammanagement.payload.dto.request.TaskRequest;
import com.ustore.teammanagement.payload.dto.request.TaskUpdateRequest;
import com.ustore.teammanagement.payload.dto.response.TaskResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

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
    ResponseEntity<TaskResponse> createTask(TaskRequest request);

    @Operation(
            summary = "Filter tasks",
            description = "Filters tasks by title, project, priority, status, assignee, due date, etc.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Filtered list of tasks",
                            content = @Content(schema = @Schema(implementation = Page.class)))
            }
    )
    ResponseEntity<Page<TaskResponse>> filterTasks(TaskFilterRequest filter, Pageable pageable);

    @Operation(
            summary = "Update an existing task",
            description = "Allows ADMIN and MANAGER users to update a task.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Task successfully updated",
                            content = @Content(schema = @Schema(implementation = TaskResponse.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "404", description = "Task not found")
            }
    )
    ResponseEntity<TaskResponse> updateTask(
            @Parameter(description = "Task ID") UUID id,
            TaskUpdateRequest request);

    @Operation(
            summary = "Delete a task",
            description = "Allows ADMIN and MANAGER users to delete a task.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Task successfully deleted"),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "404", description = "Task not found")
            }
    )
    ResponseEntity<Void> deleteTask(@Parameter(description = "Task ID") UUID id);
}
