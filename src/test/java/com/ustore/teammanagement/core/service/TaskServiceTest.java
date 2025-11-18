package com.ustore.teammanagement.core.service;

import com.ustore.teammanagement.core.entity.Member;
import com.ustore.teammanagement.core.entity.Task;
import com.ustore.teammanagement.core.enums.MemberStatus;
import com.ustore.teammanagement.core.enums.Priority;
import com.ustore.teammanagement.core.enums.Role;
import com.ustore.teammanagement.core.enums.TaskStatus;
import com.ustore.teammanagement.core.repository.MemberRepository;
import com.ustore.teammanagement.core.repository.TaskRepository;
import com.ustore.teammanagement.exceptions.ResourceNotFoundException;
import com.ustore.teammanagement.payload.dto.request.TaskRequest;
import com.ustore.teammanagement.payload.dto.response.TaskResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    private Member memberLogado;
    private Member assignee;

    @BeforeEach
    void setup() {

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        memberLogado = new Member();
        memberLogado.setId(UUID.randomUUID());
        memberLogado.setEmail("admin@example.com");
        memberLogado.setRole(Role.ADMIN);

        assignee = new Member();
        assignee.setId(UUID.randomUUID());
        assignee.setEmail("dev@example.com");
        assignee.setStatus(MemberStatus.ACTIVE);
    }

    private TaskRequest buildRequest() {
        return new TaskRequest(
                "Criar API",
                "Implementar módulo de autenticação",
                TaskStatus.TO_DO,
                Priority.HIGH,
                LocalDate.now(),
                "WebApp",
                List.of("backend", "auth"),
                assignee.getId()
        );
    }

    @Test
    @DisplayName("ADMIN deve conseguir criar uma tarefa")
    void createTask_success_admin() throws Exception {

        TaskRequest request = buildRequest();

        when(authentication.getName()).thenReturn(memberLogado.getEmail());
        when(memberRepository.findByEmail(memberLogado.getEmail()))
                .thenReturn(Optional.of(memberLogado));

        when(memberRepository.findById(assignee.getId()))
                .thenReturn(Optional.of(assignee));

        Task savedTask = request.toTask();
        savedTask.setId(UUID.randomUUID());
        savedTask.setCreatedBy(memberLogado);
        savedTask.setAssignee(assignee);

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        TaskResponse response = taskService.createTask(request);

        assertNotNull(response);
        assertEquals("Criar API", response.title());
        assertEquals(TaskStatus.TO_DO, response.status());
        assertEquals("WebApp", response.project());
    }

    @Test
    @DisplayName("Usuário comum NÃO pode criar tarefa")
    void createTask_accessDenied() {

        memberLogado.setRole(Role.MEMBER);

        when(authentication.getName()).thenReturn(memberLogado.getEmail());
        when(memberRepository.findByEmail(memberLogado.getEmail()))
                .thenReturn(Optional.of(memberLogado));

        TaskRequest request = buildRequest();

        assertThrows(AccessDeniedException.class, () -> taskService.createTask(request));
    }


    @Test
    @DisplayName("Deve lançar erro quando o usuário logado não existe no banco")
    void createTask_memberLogadoNotFound() {

        when(authentication.getName()).thenReturn("admin@example.com");
        when(memberRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        TaskRequest request = buildRequest();

        assertThrows(ResourceNotFoundException.class, () -> taskService.createTask(request));
    }

    @Test
    @DisplayName("Deve lançar erro quando o assignee não existe")
    void createTask_assigneeNotFound() {

        when(authentication.getName()).thenReturn(memberLogado.getEmail());
        when(memberRepository.findByEmail(memberLogado.getEmail()))
                .thenReturn(Optional.of(memberLogado));

        when(memberRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        TaskRequest request = buildRequest();

        assertThrows(ResourceNotFoundException.class, () -> taskService.createTask(request));
    }

    @Test
    @DisplayName("Não deve permitir atribuir tarefa a membro INACTIVE")
    void createTask_assigneeInactive() {

        assignee.setStatus(MemberStatus.INACTIVE);

        when(authentication.getName()).thenReturn(memberLogado.getEmail());
        when(memberRepository.findByEmail(memberLogado.getEmail()))
                .thenReturn(Optional.of(memberLogado));

        when(memberRepository.findById(assignee.getId()))
                .thenReturn(Optional.of(assignee));

        TaskRequest request = buildRequest();

        assertThrows(IllegalStateException.class, () -> taskService.createTask(request));
    }

    @Test
    @DisplayName("Deve filtrar tarefas corretamente usando Specification")
    void filterTasks_success() {

        Pageable pageable = PageRequest.of(0, 10);

        Task task = new Task();
        task.setId(UUID.randomUUID());
        task.setTitle("Teste");
        task.setProject("WebApp");

        Member assignee = new Member();
        assignee.setId(UUID.randomUUID());
        assignee.setName("João Dev");
        task.setAssignee(assignee);

        Member createdBy = new Member();
        createdBy.setId(UUID.randomUUID());
        createdBy.setName("Maria Admin");
        task.setCreatedBy(createdBy);

        Page<Task> taskPage = new PageImpl<>(List.of(task));

        when(taskRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(taskPage);

        Page<TaskResponse> result = taskService.filter(
                "Teste",
                "WebApp",
                TaskStatus.TO_DO,
                Priority.HIGH,
                "João",
                "Maria",
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                false,
                pageable
        );

        assertNotNull(result);
        assertEquals(1, result.getContent().size());

        TaskResponse response = result.getContent().get(0);

        assertEquals(task.getId(), response.id());
        assertEquals(task.getTitle(), response.title());
        assertEquals(task.getProject(), response.project());
        assertEquals(task.getAssignee().getName(), response.assignee().name());

        verify(taskRepository, times(1))
                .findAll(any(Specification.class), eq(pageable));
    }
}