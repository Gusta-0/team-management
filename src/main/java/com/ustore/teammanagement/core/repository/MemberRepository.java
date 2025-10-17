package com.ustore.teammanagement.core.repository;

import com.ustore.teammanagement.core.entity.Member;
import com.ustore.teammanagement.enums.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, UUID>, JpaSpecificationExecutor<Member> {
    Optional<Member> findByEmail(String email);

    long countByStatus(MemberStatus status);
}
