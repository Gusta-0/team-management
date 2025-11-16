package com.ustore.teammanagement.core.controller;

import com.ustore.teammanagement.core.enums.Priority;
import com.ustore.teammanagement.core.enums.TaskStatus;
import com.ustore.teammanagement.core.service.TaskService;
import com.ustore.teammanagement.payload.dto.request.TaskRequest;
import com.ustore.teammanagement.payload.dto.request.TaskUpdateRequest;
import com.ustore.teammanagement.payload.dto.response.TaskResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @InjectMocks
    private TaskController taskController;

    @Mock
    private TaskService taskService;

    @Test
    void shouldCreateTask_whenUserIsAdminOrManager() throws Exception {
        TaskRequest request = new TaskRequest(
                "Título",
                "Descrição",
                TaskStatus.TO_DO,
                Priority.MEDIUM,
                LocalDate.now(),
                "Projeto X",
                List.of("backend", "api"),
                UUID.randomUUID()
        );

        TaskResponse response = new TaskResponse(
                UUID.randomUUID(),
                "Título",
                "Descrição",
                TaskStatus.TO_DO,
                Priority.MEDIUM,
                LocalDate.now(),
                "Projeto X",
                List.of("backend", "api"),
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                null,
                null
        );

        when(taskService.createTask(request)).thenReturn(response);

        ResponseEntity<TaskResponse> result = taskController.createTask(request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(taskService, times(1)).createTask(request);
    }

    @Test
    void shouldThrowAccessDenied_whenMemberTriesToCreateTask() throws Exception {
        TaskRequest request = new TaskRequest(
                "Título",
                "Descrição",
                TaskStatus.TO_DO,
                Priority.MEDIUM,
                LocalDate.now(),
                "Projeto X",
                List.of("backend"),
                UUID.randomUUID()
        );

        doThrow(new AccessDeniedException("Acesso negado: você não tem permissão para criar tasks."))
                .when(taskService).createTask(request);

        Exception exception = assertThrows(
                AccessDeniedException.class,
                () -> taskController.createTask(request)
        );

        assertEquals("Acesso negado: você não tem permissão para criar tasks.", exception.getMessage());

        verify(taskService, times(1)).createTask(request);
    }

    @Test
    void shouldReturnFilteredTasks() {
        String title = "Bug";
        String project = "Projeto X";
        TaskStatus status =  TaskStatus.TO_DO;
        Priority priority = Priority.MEDIUM;
        String assigneeName = "João";
        String createdByName = "Maria";
        LocalDate dueDateFrom = LocalDate.now().minusDays(2);
        LocalDate dueDateTo = LocalDate.now().plusDays(5);
        Boolean onlyOverdue = false;

        Pageable pageable = mock(Pageable.class);

        TaskResponse task = new TaskResponse(
                UUID.randomUUID(),
                "Bug Login",
                "Erro ao logar",
                TaskStatus.TO_DO,
                Priority.MEDIUM,
                LocalDate.now().plusDays(1),
                "Projeto X",
                List.of("backend"),
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                null,
                null
        );

        Page<TaskResponse> page = new PageImpl<>(List.of(task));

        when(taskService.filter(
                title, project, status, priority,
                assigneeName, createdByName,
                dueDateFrom, dueDateTo,
                onlyOverdue, pageable
        )).thenReturn(page);

        ResponseEntity<Page<TaskResponse>> result = taskController.filterTasks(
                title, project, status, priority,
                assigneeName, createdByName,
                dueDateFrom, dueDateTo,
                onlyOverdue, pageable
        );

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(page, result.getBody());

        verify(taskService, times(1)).filter(
                title, project, status, priority,
                assigneeName, createdByName,
                dueDateFrom, dueDateTo,
                onlyOverdue, pageable
        );
    }

    @Test
    void shouldReturnEmptyPage_whenNoTasksFound() {
        Pageable pageable = mock(Pageable.class);
        Page<TaskResponse> emptyPage = Page.empty();

        when(taskService.filter(
                null, null, null, null,
                null, null,
                null, null,
                null, pageable
        )).thenReturn(emptyPage);

        ResponseEntity<Page<TaskResponse>> result = taskController.filterTasks(
                null, null, null, null,
                null, null,
                null, null,
                null, pageable
        );

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(emptyPage, result.getBody());

        verify(taskService, times(1)).filter(
                null, null, null, null,
                null, null,
                null, null,
                null, pageable
        );
    }

    @Test
    void shouldUpdateTaskSuccessfully() {
        UUID taskId = UUID.randomUUID();

        TaskUpdateRequest request = new TaskUpdateRequest(
                "Atualizar título",
                "Nova descrição",
                TaskStatus.IN_PROGRESSO,
                Priority.HIGH,
                LocalDate.now().plusDays(3),
                "Projeto Financeiro",
                List.of("urgent", "backend"),
                UUID.randomUUID()
        );

        TaskResponse expectedResponse = new TaskResponse(
                taskId,
                "Atualizar título",
                "Nova descrição",
                TaskStatus.IN_PROGRESSO,
                Priority.HIGH,
                LocalDate.now().plusDays(3),
                "Projeto Financeiro",
                List.of("urgent", "backend"),
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                null,
                null
        );

        when(taskService.updateTask(taskId, request))
                .thenReturn(expectedResponse);

        ResponseEntity<TaskResponse> result =
                taskController.updateTask(taskId, request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expectedResponse, result.getBody());

        verify(taskService, times(1))
                .updateTask(taskId, request);
    }

    @Test
    void shouldUpdateTaskWithPartialFields() {
        UUID id = UUID.randomUUID();

        TaskUpdateRequest request = new TaskUpdateRequest(
                null,
                "Somente descrição atualizada",
                null,
                null,
                null,
                null,
                null,
                null
        );

        TaskResponse response = new TaskResponse(
                id,
                "Título Original",
                "Somente descrição atualizada",
                TaskStatus.TO_DO,
                Priority.MEDIUM,
                LocalDate.now(),
                "Projeto X",
                List.of("tag"),
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                null,
                null
        );

        when(taskService.updateTask(id, request))
                .thenReturn(response);

        ResponseEntity<TaskResponse> result = taskController.updateTask(id, request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());

        verify(taskService).updateTask(id, request);
    }

    @Test
    void shouldDeleteTaskSuccessfully() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(taskService).deleteTask(id);

        ResponseEntity<Void> response = taskController.deleteTask(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(taskService, times(1)).deleteTask(id);
    }

    @Test
    void shouldThrowAccessDeniedExceptionWhenUserHasNoPermissionToDelete() throws Exception {
        UUID id = UUID.randomUUID();

        doThrow(new AccessDeniedException("Acesso negado"))
                .when(taskService).deleteTask(id);

        assertThrows(
                AccessDeniedException.class,
                () -> taskController.deleteTask(id)
        );

        verify(taskService, times(1)).deleteTask(id);
    }

}