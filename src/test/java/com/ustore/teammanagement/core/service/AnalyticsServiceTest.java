package com.ustore.teammanagement.core.service;

import com.ustore.teammanagement.core.entity.Member;
import com.ustore.teammanagement.core.entity.Task;
import com.ustore.teammanagement.core.enums.MemberStatus;
import com.ustore.teammanagement.core.enums.Priority;
import com.ustore.teammanagement.core.enums.TaskStatus;
import com.ustore.teammanagement.core.repository.MemberRepository;
import com.ustore.teammanagement.core.repository.TaskRepository;
import com.ustore.teammanagement.payload.dto.response.AnalyticsTaskResponse;
import com.ustore.teammanagement.payload.dto.response.MemberPerformanceResponse;
import com.ustore.teammanagement.payload.dto.response.OverviewResponse;
import com.ustore.teammanagement.payload.dto.response.ProjectProgressResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {


    @Mock
    private TaskRepository taskRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    private Task newTask(TaskStatus status, Priority priority) {
        Task t = new Task();
        t.setStatus(status);
        t.setPriority(priority);
        return t;
    }

    @Test
    void shouldReturnCorrectOverviewAnalytics() {
        List<Task> tasks = List.of(
                newTask(TaskStatus.IN_PROGRESSO, Priority.HIGH),
                newTask(TaskStatus.COMPLETED, Priority.MEDIUM),
                newTask(TaskStatus.LATE, Priority.LOW),
                newTask(TaskStatus.IN_PROGRESSO, Priority.MEDIUM)
        );

        when(taskRepository.findAll()).thenReturn(tasks);
        when(memberRepository.countByStatus(MemberStatus.ACTIVE)).thenReturn(7L);

        OverviewResponse response = analyticsService.AnalyticsOverview();

        assertEquals(2, response.activeTasks());

        assertEquals(1, response.lateTasks());

        assertEquals(25, response.completionRate());

        assertEquals(7L, response.activeMembers());
    }

    @Test
    void shouldReturnCorrectAnalyticsTasks() {
        List<Task> tasks = List.of(
                newTask(TaskStatus.IN_PROGRESSO, Priority.HIGH),
                newTask(TaskStatus.COMPLETED, Priority.MEDIUM),
                newTask(TaskStatus.IN_PROGRESSO, Priority.MEDIUM),
                newTask(TaskStatus.LATE, Priority.LOW)
        );

        when(taskRepository.findAll()).thenReturn(tasks);

        AnalyticsTaskResponse response = analyticsService.getAnalyticsTasks(7);

        assertEquals(2, response.tasksByStatus().get(TaskStatus.IN_PROGRESSO));
        assertEquals(1, response.tasksByStatus().get(TaskStatus.COMPLETED));
        assertEquals(1, response.tasksByStatus().get(TaskStatus.LATE));

        assertEquals(1, response.tasksByPriority().get(Priority.HIGH));
        assertEquals(2, response.tasksByPriority().get(Priority.MEDIUM));
        assertEquals(1, response.tasksByPriority().get(Priority.LOW));
    }

    @Test
    void shouldBuildTrendDataCorrectly() {
        int days = 7;

        List<Object[]> mockRows = List.of(
                new Object[]{LocalDate.of(2025, 1, 10), 5, 2},
                new Object[]{LocalDate.of(2025, 1, 11), 7, 3}
        );

        when(taskRepository.findTaskTrend(days)).thenReturn(mockRows);

        List<Map<String, Object>> result = analyticsService.getTrendData(days);

        assertEquals(2, result.size());

        Map<String, Object> day1 = result.get(1);

        assertEquals("2025-01-11", day1.get("date"));
        assertEquals(7L, day1.get("created"));
        assertEquals(3L, day1.get("completed"));
    }

    @Test
    void shouldBuildDepartmentPerformanceCorrectly() throws Exception {

        List<Object[]> mockRows = List.of(
                new Object[]{"TI", 10, 3},
                new Object[]{"Financeiro", 7, 5}
        );

        when(taskRepository.findDepartmentPerformance()).thenReturn(mockRows);

        Method method = AnalyticsService.class.getDeclaredMethod("buildDepartmentPerformance");
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> result =
                (List<Map<String, Object>>) method.invoke(analyticsService);

        assertEquals(2, result.size());

        Map<String, Object> finance = result.get(1);

        assertEquals("Financeiro", finance.get("department"));
        assertEquals(7L, finance.get("completed"));
        assertEquals(5L, finance.get("pending"));
    }

    @Test
    void shouldReturnMemberPerformancePage() {

        Pageable pageable = PageRequest.of(0, 10);

        Member member1 = new Member();
        member1.setId(UUID.randomUUID());
        member1.setName("João Silva");

        Member member2 = new Member();
        member2.setId(UUID.randomUUID());
        member2.setName("Maria Souza");

        List<Member> members = List.of(member1, member2);
        Page<Member> memberPage = new PageImpl<>(members, pageable, members.size());

        when(memberRepository.findAll(pageable)).thenReturn(memberPage);

        when(taskRepository.countByAssignee(member1)).thenReturn(10L);
        when(taskRepository.countByAssigneeAndStatus(member1, TaskStatus.COMPLETED)).thenReturn(7L);

        when(taskRepository.countByAssignee(member2)).thenReturn(5L);
        when(taskRepository.countByAssigneeAndStatus(member2, TaskStatus.COMPLETED)).thenReturn(3L);

        Page<MemberPerformanceResponse> response = analyticsService.getPerformance(pageable);

        assertNotNull(response);
        assertEquals(2, response.getContent().size());

        MemberPerformanceResponse r1 = response.getContent().get(0);
        assertEquals(10L, r1.tasksAssigned());
        assertEquals(7L, r1.tasksCompleted());
        assertEquals("João Silva", r1.name());

        MemberPerformanceResponse r2 = response.getContent().get(1);
        assertEquals(5L, r2.tasksAssigned());
        assertEquals(3L, r2.tasksCompleted());
        assertEquals("Maria Souza", r2.name());

        verify(memberRepository).findAll(pageable);
        verify(taskRepository, times(1)).countByAssignee(member1);
        verify(taskRepository, times(1)).countByAssigneeAndStatus(member1, TaskStatus.COMPLETED);

        verify(taskRepository, times(1)).countByAssignee(member2);
        verify(taskRepository, times(1)).countByAssigneeAndStatus(member2, TaskStatus.COMPLETED);
    }

    @Test
    void shouldReturnProjectProgressCorrectly() {

        Member member1 = new Member();
        member1.setId(UUID.randomUUID());

        Member member2 = new Member();
        member2.setId(UUID.randomUUID());

        Task t1 = new Task();
        t1.setProject("WebApp");
        t1.setStatus(TaskStatus.IN_PROGRESSO);
        t1.setPriority(Priority.HIGH);
        t1.setAssignee(member1);
        t1.setDueDate(LocalDate.of(2025, 12, 10));

        Task t2 = new Task();
        t2.setProject("WebApp");
        t2.setStatus(TaskStatus.COMPLETED);
        t2.setPriority(Priority.HIGH);
        t2.setAssignee(member2);
        t2.setDueDate(LocalDate.of(2025, 12, 5));

        Task t3 = new Task();
        t3.setProject("Mobile");
        t3.setStatus(TaskStatus.COMPLETED);
        t3.setPriority(Priority.LOW);
        t3.setAssignee(member1);
        t3.setDueDate(LocalDate.of(2025, 12, 1));

        List<Task> allTasks = List.of(t1, t2, t3);

        when(taskRepository.findAll()).thenReturn(allTasks);

        List<ProjectProgressResponse> response = analyticsService.getProjectProgress();

        assertNotNull(response);
        assertEquals(2, response.size());


        ProjectProgressResponse web = response.stream()
                .filter(p -> p.name().equals("WebApp"))
                .findFirst()
                .orElseThrow();

        assertEquals("WebApp", web.name());
        assertEquals(2, web.totalTasks());
        assertEquals(1, web.completedTasks());
        assertEquals(50.0, web.progress());
        assertEquals(2, web.teamMembers());
        assertEquals(LocalDate.of(2025, 12, 5), web.dueDate());
        assertEquals(Priority.HIGH, web.priority());
        assertEquals(TaskStatus.IN_PROGRESSO, web.status());

        ProjectProgressResponse mobile = response.stream()
                .filter(p -> p.name().equals("Mobile"))
                .findFirst()
                .orElseThrow();

        assertEquals("Mobile", mobile.name());
        assertEquals(1, mobile.totalTasks());
        assertEquals(1, mobile.completedTasks());
        assertEquals(100.0, mobile.progress());
        assertEquals(1, mobile.teamMembers());
        assertEquals(LocalDate.of(2025, 12, 1), mobile.dueDate());
        assertEquals(Priority.LOW, mobile.priority());
        assertEquals(TaskStatus.COMPLETED, mobile.status());

        verify(taskRepository, times(1)).findAll();
    }


    @Test
    void shouldReturnCompletedWhenAllTasksCompleted() {
        List<Task> tasks = List.of(
                createTask(TaskStatus.COMPLETED, LocalDate.now()),
                createTask(TaskStatus.COMPLETED, LocalDate.now().minusDays(1))
        );

        TaskStatus status = analyticsService.calculateProjectStatus(tasks);

        assertEquals(TaskStatus.COMPLETED, status);
    }

    @Test
    void shouldReturnLateWhenAnyTaskIsLate() {
        List<Task> tasks = List.of(
                createTask(TaskStatus.IN_PROGRESSO, LocalDate.now().minusDays(5)), // atrasada
                createTask(TaskStatus.IN_PROGRESSO, LocalDate.now().plusDays(3))   // ok
        );

        TaskStatus status = analyticsService.calculateProjectStatus(tasks);

        assertEquals(TaskStatus.LATE, status);
    }

    @Test
    void shouldReturnInProgressWhenNotCompletedAndNoLateTasks() {
        List<Task> tasks = List.of(
                createTask(TaskStatus.IN_PROGRESSO, LocalDate.now().plusDays(5)),
                createTask(TaskStatus.IN_PROGRESSO, LocalDate.now().plusDays(10))
        );

        TaskStatus status = analyticsService.calculateProjectStatus(tasks);

        assertEquals(TaskStatus.IN_PROGRESSO, status);
    }

    @Test
    void shouldReturnInProgressWhenTaskHasNoDueDate() {
        List<Task> tasks = List.of(
                createTask(TaskStatus.IN_PROGRESSO, null),
                createTask(TaskStatus.IN_PROGRESSO, LocalDate.now().plusDays(4))
        );

        TaskStatus status = analyticsService.calculateProjectStatus(tasks);

        assertEquals(TaskStatus.IN_PROGRESSO, status);
    }

    private Task createTask(TaskStatus status, LocalDate dueDate) {
        Task task = new Task();
        task.setStatus(status);
        task.setDueDate(dueDate);
        return task;
    }
}


