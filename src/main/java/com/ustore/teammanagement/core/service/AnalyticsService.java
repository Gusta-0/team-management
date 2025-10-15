package com.ustore.teammanagement.core.service;

import com.ustore.teammanagement.core.entity.Task;
import com.ustore.teammanagement.core.repository.MemberRepository;
import com.ustore.teammanagement.core.repository.TaskRepository;
import com.ustore.teammanagement.enums.MemberStatus;
import com.ustore.teammanagement.enums.Priority;
import com.ustore.teammanagement.enums.TaskStatus;
import com.ustore.teammanagement.payload.dto.response.AnalyticsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
    private final TaskRepository taskRepository;
    private final MemberRepository memberRepository;

    public AnalyticsResponse getAnalytics() {
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

        Map<TaskStatus, Long> tasksByStatus = allTasks.stream()
                .collect(Collectors.groupingBy(Task::getStatus, Collectors.counting()));

        Map<Priority, Long> tasksByPriority = allTasks.stream()
                .collect(Collectors.groupingBy(Task::getPriority, Collectors.counting()));

        List<Map<String, Object>> completionTrend = getTrendData(30);

        List<Map<String, Object>> departmentPerformance = buildDepartmentPerformance();

        return new AnalyticsResponse(
                activeTasks,
                lateTasks,
                Math.round(completionRate),
                activeMembers,
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

}
