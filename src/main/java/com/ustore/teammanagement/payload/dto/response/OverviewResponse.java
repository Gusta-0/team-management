package com.ustore.teammanagement.payload.dto.response;

import com.ustore.teammanagement.core.enums.Priority;
import com.ustore.teammanagement.core.enums.TaskStatus;

import java.util.List;
import java.util.Map;

public record OverviewResponse(
        long activeTasks,
        long lateTasks,
        double completionRate,
        long activeMembers

) {}
