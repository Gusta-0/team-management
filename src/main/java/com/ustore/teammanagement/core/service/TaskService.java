package com.ustore.teammanagement.core.service;

import com.ustore.teammanagement.core.Specifications.TaskSpecification;
import com.ustore.teammanagement.core.entity.Member;
import com.ustore.teammanagement.core.enums.MemberStatus;
import com.ustore.teammanagement.core.enums.Priority;
import com.ustore.teammanagement.core.enums.Role;
import com.ustore.teammanagement.core.enums.TaskStatus;
import com.ustore.teammanagement.core.repository.MemberRepository;
import com.ustore.teammanagement.core.repository.TaskRepository;
import com.ustore.teammanagement.exceptions.ResourceNotFoundException;
import com.ustore.teammanagement.payload.dto.request.TaskRequest;
import com.ustore.teammanagement.payload.dto.request.TaskUpdateRequest;
import com.ustore.teammanagement.payload.dto.response.TaskResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final MemberRepository memberRepository;

    public TaskService(TaskRepository taskRepository, MemberRepository memberRepository) {
        this.taskRepository = taskRepository;
        this.memberRepository = memberRepository;
    }

    public TaskResponse createTask(TaskRequest request) throws AccessDeniedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        var memberLogado = memberRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("Membro logado não encontrado"));

        Role role = memberLogado.getRole();
        if (role != Role.ADMIN && role != Role.MANAGER) {
            throw new AccessDeniedException("Acesso negado. Apenas ADMIN e MANAGER podem criar tarefas.");
        }

        var task = request.toTask();
        task.setCreatedBy(memberLogado);

        Member assignee = memberRepository.findById(request.assigneeId())
                .orElseThrow(() -> new ResourceNotFoundException("Responsável não encontrado"));

        if (assignee.getStatus() == MemberStatus.INACTIVE) {
            throw new IllegalStateException("Tarefa não pode ser atribuída a um membro desativado.");
        }

        task.setAssignee(assignee);
        var savedTask = taskRepository.save(task);
        return new TaskResponse(savedTask);
    }

    public Page<TaskResponse> filter(String title, String project, TaskStatus status, Priority priority, String assigneeName, String createdByName,
                                     LocalDate dueDateFrom, LocalDate dueDateTo, Boolean onlyOverdue , Pageable pageable) {
        return taskRepository.findAll(
                TaskSpecification.withFilters(title, project, status, priority, assigneeName, createdByName, dueDateFrom, dueDateTo, onlyOverdue),
                pageable
        ).map(TaskResponse::new);
    }

    public TaskResponse updateTask(UUID taskId, TaskUpdateRequest updateRequest) {
        var task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada"));
        updateRequest.updateTask(task, updateRequest);

        if (task.getAssignee() != null && task.getAssignee().getStatus() == MemberStatus.INACTIVE) {
            throw new IllegalStateException("Tarefa não pode ser atribuída a um membro desativado.");
        }

        taskRepository.save(task);
        return new TaskResponse(task);
    }

    public void deleteTask(UUID taskId) throws AccessDeniedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String emailLogado = auth.getName();

        var member = memberRepository.findByEmail(emailLogado)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário logado não encontrado"));

        if (!(member.getRole().equals(Role.ADMIN)
                || member.getRole().equals(Role.MANAGER))) {
            throw new AccessDeniedException("Você não tem permissão para atualizar tarefas.");
        }

        var task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada"));

        taskRepository.delete(task);
    }
}
