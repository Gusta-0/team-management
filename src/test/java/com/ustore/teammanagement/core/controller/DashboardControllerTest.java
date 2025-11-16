package com.ustore.teammanagement.core.controller;

import com.ustore.teammanagement.core.service.DashboardService;
import com.ustore.teammanagement.payload.dto.response.ActivityResponse;
import com.ustore.teammanagement.payload.dto.response.DashboardResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    @Mock
    private DashboardService dashboardService;

    @InjectMocks
    private DashboardController dashboardController;

    @Test
    void shouldReturnDashboardStatsSuccessfully() {
        DashboardResponse response = new DashboardResponse(
                50L,
                20L,
                5L,
                87.5
        );

        when(dashboardService.getDashboardStats()).thenReturn(response);

        ResponseEntity<DashboardResponse> result = dashboardController.getDashboard();

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());

        DashboardResponse body = result.getBody();

        assertEquals(50L, body.totalMembers());
        assertEquals(20L, body.activeTasks());
        assertEquals(5L, body.pendingReviews());
        assertEquals(87.5, body.completionRate());

        verify(dashboardService).getDashboardStats();
    }

    @Test
    void shouldReturnRecentActivitiesSuccessfully() {
        OffsetDateTime now = OffsetDateTime.now();

        ActivityResponse activity1 = new ActivityResponse(
                "John Doe",
                "created",
                "Implementar módulo de pagamento",
                now.minusHours(1)
        );

        ActivityResponse activity2 = new ActivityResponse(
                "Maria Silva",
                "updated",
                "Corrigir bug no dashboard",
                now.minusMinutes(30)
        );

        List<ActivityResponse> activities = List.of(activity1, activity2);

        when(dashboardService.getRecentActivities()).thenReturn(activities);

        ResponseEntity<List<ActivityResponse>> result = dashboardController.getRecentActivities();

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals(2, result.getBody().size());

        ActivityResponse a1 = result.getBody().get(0);
        ActivityResponse a2 = result.getBody().get(1);

        assertEquals("John Doe", a1.authorName());
        assertEquals("created", a1.action());
        assertEquals("Implementar módulo de pagamento", a1.taskTitle());
        assertEquals(now.minusHours(1), a1.timestamp());

        assertEquals("Maria Silva", a2.authorName());
        assertEquals("updated", a2.action());
        assertEquals("Corrigir bug no dashboard", a2.taskTitle());
        assertEquals(now.minusMinutes(30), a2.timestamp());

        verify(dashboardService).getRecentActivities();
    }
}