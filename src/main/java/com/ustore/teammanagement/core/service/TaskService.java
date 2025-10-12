package com.ustore.teammanagement.core.service;

import com.ustore.teammanagement.core.Specifications.TaskSpecification;
import com.ustore.teammanagement.core.entity.Member;
import com.ustore.teammanagement.core.repository.MemberRepository;
import com.ustore.teammanagement.core.repository.TaskRepository;
import com.ustore.teammanagement.enums.Role;
import com.ustore.teammanagement.enums.TaskStatus;
import com.ustore.teammanagement.exception.ResourceNotFoundException;
import com.ustore.teammanagement.payload.dto.request.TaskFilterRequest;
import com.ustore.teammanagement.payload.dto.request.TaskRequest;
import com.ustore.teammanagement.payload.dto.request.TaskUpdateRequest;
import com.ustore.teammanagement.payload.dto.response.TaskResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final MemberRepository memberRepository;

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
        task.setStatus(TaskStatus.TO_DO);

        Member assignee = memberRepository.findById(request.assigneeId())
                .orElseThrow(() -> new ResourceNotFoundException("Responsável não encontrado"));

        task.setAssignee(assignee);
        var savedTask = taskRepository.save(task);
        return new TaskResponse(savedTask);
    }

    public Page<TaskResponse> filter(TaskFilterRequest filterRequest, Pageable pageable) {
        return taskRepository.findAll(
                TaskSpecification.withFilters(filterRequest),
                pageable
        ).map(TaskResponse::new);
    }

    public TaskResponse updateTask(UUID taskId, TaskUpdateRequest updateRequest) throws AccessDeniedException {
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

        updateRequest.updateTask(task, updateRequest);
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
