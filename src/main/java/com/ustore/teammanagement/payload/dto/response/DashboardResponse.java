package com.ustore.teammanagement.payload.dto.response;

public record DashboardResponse(
        long totalMembers,
        long activeTasks,
        long pendingReviews,
        double completionRate
) {}
