package com.ustore.teammanagement.core.service;

import com.ustore.teammanagement.core.entity.Member;
import com.ustore.teammanagement.core.entity.Task;
import com.ustore.teammanagement.core.enums.MemberStatus;
import com.ustore.teammanagement.core.enums.Priority;
import com.ustore.teammanagement.core.enums.TaskStatus;
import com.ustore.teammanagement.core.repository.MemberRepository;
import com.ustore.teammanagement.core.repository.TaskRepository;
import com.ustore.teammanagement.payload.dto.response.AnalyticsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @InjectMocks
    AnalyticsService analyticsService;

    @Mock
    MemberRepository memberRepository;

    @Mock
    TaskRepository taskRepository;

    private List<Task> allTasks;

    Task task1, task2;

    @BeforeEach
    public void setUp() {
        allTasks = new ArrayList<>();

        Member fakeAssignee1 = mock(Member.class);
        Member fakeAssignee2 = mock(Member.class);

        task1 = Task.builder()
                .id(UUID.randomUUID())
                .title("Task 1")
                .description("Description 1")
                .status(TaskStatus.COMPLETED)
                .priority(Priority.MEDIUM)
                .dueDate(LocalDate.now().minusDays(2))
                .project("Project 1")
                .tags(List.of("tag1", "tag2"))
                .assignee(fakeAssignee1)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        task2 = Task.builder()
                .id(UUID.randomUUID())
                .title("Task 2")
                .description("Description 2")
                .status(TaskStatus.LATE)
                .priority(Priority.LOW)
                .dueDate(LocalDate.now().minusDays(1))
                .project("Project 2")
                .tags(List.of("tag3"))
                .assignee(fakeAssignee2)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        allTasks.add(task1);
        allTasks.add(task2);
    }

    @Test
    void getAnalytics_shouldReturnCorrectAggregates() {
        // mocks
        when(taskRepository.findAll()).thenReturn(allTasks);
        when(memberRepository.countByStatus(MemberStatus.ACTIVE)).thenReturn(2L);

//        Object[] trendRow = new Object[]{LocalDate.of(2025, 11, 1),
//                3L,
//                1L
//        };
//        List<Object[]> trendRows = List.of(trendRow);
//        when(taskRepository.findTaskTrend(30)).thenReturn(trendRows);
//
//        Object[] deptRow = new Object[]{
//                "Legal",
//                5L,
//                2L
//        };
//        List<Object[]> deptRows = List.of(deptRow);
//        when(taskRepository.findDepartmentPerformance()).thenReturn(deptRows);


        // execução
        AnalyticsResponse response = analyticsService.getAnalytics();

        // ----------- ASSERTS CORRETOS ------------

        // activeTasks = nenhuma (nenhuma é IN_PROGRESS)
        assertEquals(0L, response.activeTasks());

        // lateTasks = 1
        assertEquals(1L, response.lateTasks());

        // completedOnTime = 1, total = 2 => 50%
        assertEquals(50L, response.completionRate());

        // membros ativos mockados
        assertEquals(2L, response.activeMembers());

        // status map
        Map<TaskStatus, Long> byStatus = response.tasksByStatus();
        assertEquals(1L, byStatus.get(TaskStatus.COMPLETED));
        assertEquals(1L, byStatus.get(TaskStatus.LATE));

        // priority map
        Map<Priority, Long> byPriority = response.tasksByPriority();
        assertEquals(1L, byPriority.get(Priority.MEDIUM));
        assertEquals(1L, byPriority.get(Priority.LOW));

        // trend
        List<Map<String, Object>> trend = response.completionTrend();
        assertEquals(1, trend.size());
        assertEquals("2025-11-01", trend.get(0).get("date"));
        assertEquals(3L, trend.get(0).get("created"));
        assertEquals(1L, trend.get(0).get("completed"));

        // department performance
        List<Map<String, Object>> dept = response.departmentPerformance();
        assertEquals(1, dept.size());
        assertEquals("Legal", dept.get(0).get("department"));
        assertEquals(5L, dept.get(0).get("completed"));
        assertEquals(2L, dept.get(0).get("pending"));

        // verify interactions
        verify(taskRepository).findAll();
        verify(taskRepository).findTaskTrend(30);
        verify(taskRepository).findDepartmentPerformance();
        verify(memberRepository).countByStatus(MemberStatus.ACTIVE);
    }
}
