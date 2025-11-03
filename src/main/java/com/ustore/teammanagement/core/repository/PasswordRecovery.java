package com.ustore.teammanagement.core.repository;

import com.ustore.teammanagement.core.entity.PasswordRecoveryToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordRecovery extends JpaRepository<PasswordRecoveryToken, Long> {
    Optional<PasswordRecoveryToken> findByTokenAndUsedFalse(String token);
}
