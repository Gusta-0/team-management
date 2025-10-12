package com.ustore.teammanagement.core.controller;


import com.ustore.teammanagement.config.TaskAPI;
import com.ustore.teammanagement.core.service.TaskService;
import com.ustore.teammanagement.payload.dto.request.TaskFilterRequest;
import com.ustore.teammanagement.payload.dto.request.TaskRequest;
import com.ustore.teammanagement.payload.dto.request.TaskUpdateRequest;
import com.ustore.teammanagement.payload.dto.response.TaskResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.AccessDeniedException;

import java.util.UUID;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController implements TaskAPI {
    private final TaskService taskService;


    @PostMapping
    @Override
    public ResponseEntity<TaskResponse> createTask(@RequestBody TaskRequest request) throws AccessDeniedException {
        TaskResponse response = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Override
    public ResponseEntity<Page<TaskResponse>> filterTasks(TaskFilterRequest filter, Pageable pageable) {
        Page<TaskResponse> page = taskService.filter(filter, pageable);
        return ResponseEntity.ok(page);
    }

    @PutMapping("/{id}")
    @Override
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable UUID id,
            @RequestBody TaskUpdateRequest request) throws AccessDeniedException {
        TaskResponse response = taskService.updateTask(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> deleteTask(@PathVariable UUID id) throws AccessDeniedException {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
