package com.ustore.teammanagement.core.controller;


import com.ustore.teammanagement.config.TaskAPI;
import com.ustore.teammanagement.core.enums.Priority;
import com.ustore.teammanagement.core.enums.TaskStatus;
import com.ustore.teammanagement.core.service.TaskService;
import com.ustore.teammanagement.payload.dto.request.TaskRequest;
import com.ustore.teammanagement.payload.dto.request.TaskUpdateRequest;
import com.ustore.teammanagement.payload.dto.response.TaskResponse;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController implements TaskAPI {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request) throws AccessDeniedException {
        TaskResponse response = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Override
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
            @ParameterObject Pageable pageable) {
        Page<TaskResponse> page = taskService.filter(title, project, status, priority, assigneeName, createdByName, dueDateFrom, dueDateTo, onlyOverdue, pageable);
        return ResponseEntity.ok(page);
    }

    @PutMapping("/{id}")
    @Override
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable UUID id,
            @Valid @RequestBody TaskUpdateRequest request) {
        TaskResponse response = taskService.updateTask(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID id) throws AccessDeniedException {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
