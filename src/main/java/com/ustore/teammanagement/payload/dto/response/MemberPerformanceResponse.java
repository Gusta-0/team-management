package com.ustore.teammanagement.payload.dto.response;

import com.ustore.teammanagement.core.entity.Member;
import com.ustore.teammanagement.core.enums.Role;

import java.util.UUID;

public record MemberPerformanceResponse(
        UUID id,
        String name,
        Role role,
        String department,
        long tasksCompleted,
        long tasksAssigned,
        double completionRate,
        double avgCompletionTime,
        int trend
) {
    public static MemberPerformanceResponse fromEntity(
            Member member,
            long tasksCompleted,
            long tasksAssigned,
            double avgCompletionTime,
            int trend
    ) {
        double completionRate = tasksAssigned > 0
                ? (tasksCompleted * 100.0 / tasksAssigned)
                : 0.0;

        return new MemberPerformanceResponse(
                member.getId(),
                member.getName(),
                member.getRole(),
                member.getDepartment(),
                tasksCompleted,
                tasksAssigned,
                completionRate,
                avgCompletionTime,
                trend
        );
    }
}
