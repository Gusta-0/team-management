package com.ustore.teammanagement.core.service;

import com.ustore.teammanagement.core.entity.Task;
import com.ustore.teammanagement.core.repository.MemberRepository;
import com.ustore.teammanagement.core.repository.TaskRepository;
import com.ustore.teammanagement.enums.MemberStatus;
import com.ustore.teammanagement.enums.TaskStatus;
import com.ustore.teammanagement.payload.dto.response.ActivityResponse;
import com.ustore.teammanagement.payload.dto.response.DashboardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final MemberRepository memberRepository;
    private final TaskRepository taskRepository;

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
        return taskRepository.findTop5ByOrderByUpdatedAtDesc()
                .stream()
                .map(task -> new ActivityResponse(
                        task.getCreatedBy() != null ? task.getCreatedBy().getName() : "Desconhecido",
                        getActionLabel(task),
                        task.getTitle(),
                        task.getUpdatedAt()
                ))
                .collect(Collectors.toList());
    }

    private String getActionLabel(Task task) {
        return switch (task.getStatus()) {
            case COMPLETED -> "concluiu";
            case REVISION -> "enviou para revisão";
            case IN_PROGRESSO -> "atualizou";
            default -> "criou";
        };
    }
}
