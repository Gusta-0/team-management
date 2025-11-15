package com.ustore.teammanagement.core.controller;

import com.ustore.teammanagement.core.enums.Priority;
import com.ustore.teammanagement.core.enums.Role;
import com.ustore.teammanagement.core.enums.TaskStatus;
import com.ustore.teammanagement.core.service.AnalyticsService;
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
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsControllerTest {

    @InjectMocks
    private AnalyticsController analyticsController;

    @Mock
    private AnalyticsService analyticsService;

    @Test
    void shouldReturnOverviewSuccessfull() {
        OverviewResponse response = new OverviewResponse(10L, 5L, 3L, 7L);

        when(analyticsService.AnalyticsOverview()).thenReturn(response);

        ResponseEntity<OverviewResponse> result = analyticsController.Overview();

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals(10L, result.getBody().activeTasks());
        assertEquals(5L, result.getBody().lateTasks());
        assertEquals(3L, result.getBody().completionRate());
        assertEquals(7L, result.getBody().activeMembers());

        verify(analyticsService).AnalyticsOverview();
    }

    @Test
    void shouldGetAnalyticsTasksSuccessfully() {
        int days = 30;

        Map<TaskStatus, Long> tasksByStatus = Map.of(
                TaskStatus.TO_DO, 5L,
                TaskStatus.COMPLETED, 8L,
                TaskStatus.LATE, 2L
        );

        Map<Priority, Long> tasksByPriority = Map.of(
                Priority.HIGH, 4L,
                Priority.MEDIUM, 6L,
                Priority.LOW, 3L
        );

        List<Map<String, Object>> completionTrend = List.of(
                Map.of("date", "2025-11-01", "completed", 3),
                Map.of("date", "2025-11-02", "completed", 5)
        );

        List<Map<String, Object>> departmentPerformance = List.of(
                Map.of("department", "TI", "progress", 80),
                Map.of("department", "Financeiro", "progress", 60)
        );

        AnalyticsTaskResponse response = new AnalyticsTaskResponse(
                tasksByStatus,
                tasksByPriority,
                completionTrend,
                departmentPerformance
        );

        when(analyticsService.getAnalyticsTasks(days)).thenReturn(response);

        ResponseEntity<AnalyticsTaskResponse> result = analyticsController.getAnalyticsTasks(days);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals(5L, result.getBody().tasksByStatus().get(TaskStatus.TO_DO));
        assertEquals(4L, result.getBody().tasksByPriority().get(Priority.HIGH));
        assertEquals("2025-11-01", result.getBody().completionTrend().get(0).get("date"));
        assertEquals("TI", result.getBody().departmentPerformance().get(0).get("department"));

        verify(analyticsService).getAnalyticsTasks(days);
    }

    @Test
    void shouldGetMembersAnalysisSuccessfully() {
        Pageable pageable = PageRequest.of(0, 10);

        MemberPerformanceResponse member = new MemberPerformanceResponse(
                UUID.randomUUID(),
                "John Doe",
                Role.ADMIN,
                "TI",
                80L,
                100L,
                80.0,
                24.5,
                5
        );

        Page<MemberPerformanceResponse> page = new PageImpl<>(
                List.of(member),
                pageable,
                1
        );

        when(analyticsService.getPerformance(pageable)).thenReturn(page);

        ResponseEntity<Map<String, Object>> result = analyticsController.getMembersAnalysis(pageable);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());

        Map<String, Object> body = result.getBody();

        List<?> membersList = (List<?>) body.get("members");
        assertEquals(1, membersList.size());

        MemberPerformanceResponse returned = (MemberPerformanceResponse) membersList.get(0);
        assertEquals("John Doe", returned.name());
        assertEquals(Role.ADMIN, returned.role());
        assertEquals("TI", returned.department());

        assertEquals(0, body.get("page"));
        assertEquals(10, body.get("size"));
        assertEquals(1, body.get("totalPages"));
        assertEquals(1L, body.get("totalElements"));

        verify(analyticsService).getPerformance(pageable);
    }

    @Test
    void shouldGetProjectsAnalysisSuccessfully() {
        ProjectProgressResponse project1 = new ProjectProgressResponse(
                UUID.randomUUID(),
                "Sistema Financeiro",
                "Projeto de controle financeiro",
                75.0,
                10L,
                7L,
                4L,
                null,
                TaskStatus.IN_PROGRESSO,
                Priority.HIGH
        );

        ProjectProgressResponse project2 = new ProjectProgressResponse(
                UUID.randomUUID(),
                "Portal Web",
                "Projeto do portal institucional",
                50.0,
                15L,
                2L,
                6L,
                null,
                TaskStatus.TO_DO,
                Priority.MEDIUM
        );

        List<ProjectProgressResponse> projects = List.of(project1, project2);

        when(analyticsService.getProjectProgress()).thenReturn(projects);

        ResponseEntity<Map<String, Object>> result = analyticsController.getProjectsAnalysis();

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());

        Map<String, Object> body = result.getBody();

        List<?> returnedProjects = (List<?>) body.get("projects");
        assertEquals(2, returnedProjects.size());

        ProjectProgressResponse p1 = (ProjectProgressResponse) returnedProjects.get(0);
        ProjectProgressResponse p2 = (ProjectProgressResponse) returnedProjects.get(1);

        assertEquals("Sistema Financeiro", p1.name());
        assertEquals("Portal Web", p2.name());


        assertEquals(75.0, p1.progress());
        assertEquals(50.0, p2.progress());


        assertEquals(10L, p1.totalTasks());
        assertEquals(7L, p1.completedTasks());
        assertEquals(4L, p1.teamMembers());

        assertEquals(TaskStatus.IN_PROGRESSO, p1.status());
        assertEquals(Priority.HIGH, p1.priority());

        assertEquals(15L, p2.totalTasks());
        assertEquals(2L, p2.completedTasks());
        assertEquals(6L, p2.teamMembers());

        assertEquals(TaskStatus.TO_DO, p2.status());
        assertEquals(Priority.MEDIUM, p2.priority());

        verify(analyticsService).getProjectProgress();
    }
}