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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {
    private final TaskRepository taskRepository;
    private final MemberRepository memberRepository;

    public AnalyticsService(TaskRepository taskRepository, MemberRepository memberRepository) {
        this.taskRepository = taskRepository;
        this.memberRepository = memberRepository;
    }

    public OverviewResponse AnalyticsOverview() {
        List<Task> allTasks = taskRepository.findAll();
        long activeMembers = memberRepository.countByStatus(MemberStatus.ACTIVE);
        long totalTasks = allTasks.size();

        long activeTasks = allTasks.stream()
                .filter(t -> t.getStatus() != TaskStatus.COMPLETED && t.getStatus() != TaskStatus.LATE)
                .count();

        long lateTasks = allTasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.LATE)
                .count();

        long completedOnTime = allTasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.COMPLETED)
                .count();

        double completionRate = totalTasks == 0 ? 0 :
                (double) completedOnTime / totalTasks * 100;

        return new OverviewResponse(
                activeTasks,
                lateTasks,
                Math.round(completionRate),
                activeMembers
        );
    }

    public AnalyticsTaskResponse getAnalyticsTasks(int days) {
        List<Task> allTasks = taskRepository.findAll();

        Map<TaskStatus, Long> tasksByStatus = allTasks.stream()
                .collect(Collectors.groupingBy(Task::getStatus, Collectors.counting()));

        Map<Priority, Long> tasksByPriority = allTasks.stream()
                .collect(Collectors.groupingBy(Task::getPriority, Collectors.counting()));

        List<Map<String, Object>> completionTrend = getTrendData(days);

        List<Map<String, Object>> departmentPerformance = buildDepartmentPerformance();

        return new AnalyticsTaskResponse(
                tasksByStatus,
                tasksByPriority,
                completionTrend,
                departmentPerformance
        );
    }

    public List<Map<String, Object>> getTrendData(int days) {
        List<Object[]> rows = taskRepository.findTaskTrend(days);

        List<Map<String, Object>> trend = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("date", row[0].toString().substring(0, 10));
            map.put("created", ((Number) row[1]).longValue());
            map.put("completed", ((Number) row[2]).longValue());
            trend.add(map);
        }
        return trend;
    }

    private List<Map<String, Object>> buildDepartmentPerformance() {
        List<Object[]> rows = taskRepository.findDepartmentPerformance();

        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("department", row[0]);
            map.put("completed", ((Number) row[1]).longValue());
            map.put("pending", ((Number) row[2]).longValue());
            result.add(map);
        }
        return result;
    }

    public Page<MemberPerformanceResponse> getPerformance(Pageable pageable) {
        return memberRepository.findAll(pageable)
                .map(member -> {
                    long tasksAssigned = taskRepository.countByAssignee(member);
                    long tasksCompleted = taskRepository.countByAssigneeAndStatus(member, TaskStatus.COMPLETED);

                    double avgCompletionTime = 0;
                    int trend = 0;

                    return MemberPerformanceResponse.fromEntity(
                            member,
                            tasksCompleted,
                            tasksAssigned,
                            avgCompletionTime,
                            trend
                    );
                });
    }


    public List<ProjectProgressResponse> getProjectProgress() {

        List<Task> tasks = taskRepository.findAll();

        // Agrupar tarefas por nome do projeto
        Map<String, List<Task>> grouped = tasks.stream()
                .collect(Collectors.groupingBy(Task::getProject));

        return grouped.entrySet().stream()
                .map(entry -> {

                    String projectName = entry.getKey();
                    List<Task> projectTasks = entry.getValue();

                    long totalTasks = projectTasks.size();
                    long completedTasks = projectTasks.stream()
                            .filter(t -> t.getStatus() == TaskStatus.COMPLETED)
                            .count();

                    double progress = totalTasks > 0
                            ? (completedTasks * 100.0 / totalTasks)
                            : 0.0;

                    LocalDate dueDate = projectTasks.stream()
                            .map(Task::getDueDate)
                            .filter(Objects::nonNull)
                            .min(LocalDate::compareTo)
                            .orElse(null);

                    long teamMembers = projectTasks.stream()
                            .map(Task::getAssignee)
                            .filter(Objects::nonNull)
                            .map(Member::getId)
                            .distinct()
                            .count();

                    Priority priority = projectTasks.stream()
                            .map(Task::getPriority)
                            .filter(Objects::nonNull)
                            .findFirst()
                            .orElse(Priority.MEDIUM);

                    TaskStatus status = calculateProjectStatus(dueDate);

                    return new ProjectProgressResponse(
                            UUID.randomUUID(),
                            projectName,
                            "Projeto " + projectName,
                            progress,
                            totalTasks,
                            completedTasks,
                            teamMembers,
                            dueDate,
                            status,
                            priority
                    );
                })
                .toList();
    }


    private TaskStatus calculateProjectStatus(LocalDate dueDate) {
        if (dueDate == null) return TaskStatus.IN_PROGRESSO; // ou outro status
        return dueDate.isBefore(LocalDate.now())
                ? TaskStatus.LATE
                : TaskStatus.IN_PROGRESSO;
    }

}
