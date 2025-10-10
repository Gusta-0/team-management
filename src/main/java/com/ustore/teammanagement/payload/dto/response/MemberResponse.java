package com.ustore.teammanagement.payload.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ustore.teammanagement.core.entity.Member;
import com.ustore.teammanagement.enums.MemberStatus;
import com.ustore.teammanagement.enums.Role;

import java.time.OffsetDateTime;
import java.util.UUID;

public record MemberResponse(
        UUID id,
        String name,
        String email,
        Role role,
        String department,
        String phone,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        OffsetDateTime joinDate,
        MemberStatus status,
        String image
) {
    public MemberResponse(Member member) {
        this(
                member.getId(),
                member.getName(),
                member.getEmail(),
                member.getRole(),
                member.getDepartment(),
                member.getPhone(),
                member.getJoinDate(),
                member.getStatus(),
                member.getImage()
        );
    }
}
