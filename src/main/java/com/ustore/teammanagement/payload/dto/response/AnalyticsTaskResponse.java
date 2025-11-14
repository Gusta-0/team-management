package com.ustore.teammanagement.payload.dto.response;

import com.ustore.teammanagement.core.enums.Priority;
import com.ustore.teammanagement.core.enums.TaskStatus;

import java.util.List;
import java.util.Map;

public record AnalyticsTaskResponse(
        Map<TaskStatus, Long> tasksByStatus,
        Map<Priority, Long> tasksByPriority,
        List<Map<String, Object>> completionTrend,
        List<Map<String, Object>> departmentPerformance
) {
}
