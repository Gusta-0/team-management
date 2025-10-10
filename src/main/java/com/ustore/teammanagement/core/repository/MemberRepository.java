package com.ustore.teammanagement.core.repository;

import com.ustore.teammanagement.core.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, UUID>, JpaSpecificationExecutor<Member> {
    Optional<Member> findByEmail(String email);

    @Transactional
    void deleteByEmail(String email);
}
