package com.ustore.teammanagement.core.service;

import com.ustore.teammanagement.core.entity.Task;
import com.ustore.teammanagement.core.enums.TaskStatus;
import com.ustore.teammanagement.core.repository.TaskRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LateTaskSchedulerTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private LateTaskScheduler scheduler;

    @Test
    @DisplayName("Deve marcar tarefas atrasadas como LATE e salvar no repositório")
    void shouldMarkLateTasks() {

        LocalDate today = LocalDate.now();

        Task t1 = new Task();
        t1.setStatus(TaskStatus.TO_DO);
        t1.setDueDate(today.minusDays(1));

        Task t2 = new Task();
        t2.setStatus(TaskStatus.IN_PROGRESSO);
        t2.setDueDate(today.minusDays(5));

        Task t3 = new Task();
        t3.setStatus(TaskStatus.REVISION);
        t3.setDueDate(today.minusDays(2));

        List<Task> overdueTasks = List.of(t1, t2, t3);

        when(taskRepository.findByStatusInAndDueDateBefore(
                anyList(),
                eq(today)
        )).thenReturn(overdueTasks);

        scheduler.markLateTasks();

        assertEquals(TaskStatus.LATE, t1.getStatus());
        assertEquals(TaskStatus.LATE, t2.getStatus());
        assertEquals(TaskStatus.LATE, t3.getStatus());

        verify(taskRepository, times(1))
                .findByStatusInAndDueDateBefore(
                        List.of(TaskStatus.TO_DO, TaskStatus.IN_PROGRESSO, TaskStatus.REVISION),
                        today
                );

        verify(taskRepository, times(1))
                .saveAll(overdueTasks);
    }

    @Test
    @DisplayName("Não deve salvar nada se não houver tarefas atrasadas")
    void shouldDoNothingWhenNoOverdueTasks() {

        LocalDate today = LocalDate.now();

        when(taskRepository.findByStatusInAndDueDateBefore(anyList(), eq(today)))
                .thenReturn(List.of());

        scheduler.markLateTasks();

        verify(taskRepository, times(1))
                .findByStatusInAndDueDateBefore(anyList(), eq(today));

        verify(taskRepository, never()).saveAll(anyList());
    }
}
