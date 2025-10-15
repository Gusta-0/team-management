package com.ustore.teammanagement.core.service;

import com.ustore.teammanagement.core.entity.Task;
import com.ustore.teammanagement.core.repository.TaskRepository;
import com.ustore.teammanagement.enums.TaskStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class LateTaskScheduler {

    private final TaskRepository taskRepository;

    public LateTaskScheduler(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }


    @Scheduled(cron = "0 0 2 * * *")
    public void markLateTasks() {
        LocalDate today = LocalDate.now();

        List<Task> overdueTasks = taskRepository.findByStatusInAndDueDateBefore(
                List.of(TaskStatus.TO_DO, TaskStatus.IN_PROGRESSO, TaskStatus.REVISION),
                today
        );

        for (Task task : overdueTasks) {
            task.setStatus(TaskStatus.LATE);
        }

        taskRepository.saveAll(overdueTasks);
    }
}
