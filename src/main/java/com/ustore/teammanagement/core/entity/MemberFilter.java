package com.ustore.teammanagement.core.entity;

import com.ustore.teammanagement.core.enums.MemberStatus;
import com.ustore.teammanagement.core.enums.Role;

public record MemberFilter(
        String name,
        String email,
        String department,
        MemberStatus status,
        Role role
) {
}
