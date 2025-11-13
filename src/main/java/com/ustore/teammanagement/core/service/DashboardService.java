package com.ustore.teammanagement.core.service;

import com.ustore.teammanagement.core.entity.Task;
import com.ustore.teammanagement.core.enums.MemberStatus;
import com.ustore.teammanagement.core.enums.TaskStatus;
import com.ustore.teammanagement.core.repository.MemberRepository;
import com.ustore.teammanagement.core.repository.TaskRepository;
import com.ustore.teammanagement.payload.dto.response.ActivityResponse;
import com.ustore.teammanagement.payload.dto.response.DashboardResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    private final MemberRepository memberRepository;
    private final TaskRepository taskRepository;

    public DashboardService(MemberRepository memberRepository, TaskRepository taskRepository) {
        this.memberRepository = memberRepository;
        this.taskRepository = taskRepository;
    }

    public DashboardResponse getDashboardStats() {
        long totalMembers = memberRepository.countByStatus(MemberStatus.ACTIVE);

        long activeTasks = taskRepository.countByStatusNot(TaskStatus.COMPLETED);

        long pendingReviews = taskRepository.countByStatusIn(
                List.of(TaskStatus.TO_DO, TaskStatus.IN_PROGRESSO, TaskStatus.REVISION)
        );

        long totalTasks = taskRepository.count();

        double completionRate = totalTasks == 0
                ? 0
                : (double) (totalTasks - activeTasks) / totalTasks * 100;

        return new DashboardResponse(
                totalMembers,
                activeTasks,
                pendingReviews,
                Math.round(completionRate)
        );
    }

    public List<ActivityResponse> getRecentActivities() {
        Pageable pageable = PageRequest.of(0, 5);

        return taskRepository.findTop5RecentTasks(pageable)
                .stream()
                .filter(task -> task.getStatus() == TaskStatus.TO_DO || task.getStatus() == TaskStatus.COMPLETED)
                .map(task -> new ActivityResponse(
                        task.getCreatedBy() != null ? task.getCreatedBy().getName() : "Desconhecido",
                        getActionLabel(task),
                        task.getTitle(),
                        task.getUpdatedAt() != null ? task.getUpdatedAt() : task.getCreatedAt()
                ))
                .toList();
    }


    private String getActionLabel(Task task) {
        return switch (task.getStatus()) {
            case COMPLETED -> "concluiu a tarefa";
            case TO_DO -> "criou a tarefa";
            default -> "";
        };
    }
}
