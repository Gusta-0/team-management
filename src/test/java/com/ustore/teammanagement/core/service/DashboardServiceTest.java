package com.ustore.teammanagement.core.service;

import com.ustore.teammanagement.core.entity.Member;
import com.ustore.teammanagement.core.entity.Task;
import com.ustore.teammanagement.core.enums.MemberStatus;
import com.ustore.teammanagement.core.enums.TaskStatus;
import com.ustore.teammanagement.core.repository.MemberRepository;
import com.ustore.teammanagement.core.repository.TaskRepository;
import com.ustore.teammanagement.payload.dto.response.ActivityResponse;
import com.ustore.teammanagement.payload.dto.response.DashboardResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        reset(memberRepository, taskRepository);
    }

    @Test
    void testGetDashboardStatus() {
        when(memberRepository.countByStatus(MemberStatus.ACTIVE)).thenReturn(10L);
        when(taskRepository.countByStatusNot(TaskStatus.COMPLETED)).thenReturn(6L);
        when(taskRepository.countByStatusIn(List.of(
                TaskStatus.TO_DO,
                TaskStatus.IN_PROGRESSO,
                TaskStatus.REVISION
        ))).thenReturn(4L);
        when(taskRepository.count()).thenReturn(12L);

        DashboardResponse response = dashboardService.getDashboardStats();

        assertEquals(10L, response.totalMembers());
        assertEquals(6L, response.activeTasks());
        assertEquals(4L, response.pendingReviews());

        assertEquals(Math.round((12 - 6) / 12.0 * 100), response.completionRate());
    }

    @Test
    void testGetDashboardStatus_NoTasks() {
        when(memberRepository.countByStatus(MemberStatus.ACTIVE)).thenReturn(3L);
        when(taskRepository.countByStatusNot(TaskStatus.COMPLETED)).thenReturn(0L);
        when(taskRepository.countByStatusIn(any())).thenReturn(0L);
        when(taskRepository.count()).thenReturn(0L);

        DashboardResponse response = dashboardService.getDashboardStats();

        assertEquals(3L, response.totalMembers());
        assertEquals(0L, response.activeTasks());
        assertEquals(0L, response.pendingReviews());
        assertEquals(0, response.completionRate());
    }

    @Test
    void testGetRecentActivities() {
        Member creator = new Member();
        creator.setName("Alice");

        Task t1 = new Task();
        t1.setTitle("Criar API");
        t1.setStatus(TaskStatus.TO_DO);
        t1.setCreatedBy(creator);

        Task t2 = new Task();
        t2.setTitle("Revisar código");
        t2.setStatus(TaskStatus.COMPLETED);
        t2.setCreatedBy(creator);

        Task t3 = new Task();
        t3.setTitle("Em andamento");
        t3.setStatus(TaskStatus.IN_PROGRESSO);

        Pageable pageable = PageRequest.of(0, 5);

        when(taskRepository.findTop5RecentTasks(pageable))
                .thenReturn(List.of(t1, t2, t3));

        List<ActivityResponse> response = dashboardService.getRecentActivities();

        assertEquals(2, response.size());

        ActivityResponse r1 = response.get(0);
        assertEquals("Alice", t1.getCreatedBy().getName());
        assertEquals("criou a tarefa", r1.action());
        assertEquals("Criar API", t1.getTitle());

        ActivityResponse r2 = response.get(1);
        assertEquals("Alice", t2.getCreatedBy().getName());
        assertEquals("concluiu a tarefa", r2.action());
        assertEquals("Revisar código", t2.getTitle());
    }

    @Test
    void testGetRecentActivities_UnknownCreator() {
        Task t = new Task();
        t.setTitle("Tarefa misteriosa");
        t.setStatus(TaskStatus.TO_DO);
        t.setCreatedBy(null);

        Pageable pageable = PageRequest.of(0, 5);

        when(taskRepository.findTop5RecentTasks(pageable))
                .thenReturn(List.of(t));

        List<ActivityResponse> response = dashboardService.getRecentActivities();

        assertEquals(1, response.size());
        assertEquals("Desconhecido", response.get(0).authorName());
    }
}