package com.ustore.teammanagement.core.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String token;

    @OneToOne
    private Member member;

    private LocalDateTime expiryDate;

    public PasswordResetToken() {
    }

    public PasswordResetToken(UUID id, String token, Member member, LocalDateTime expiryDate) {
        this.id = id;
        this.token = token;
        this.member = member;
        this.expiryDate = expiryDate;
    }

    public boolean isExpired() {
        return expiryDate.isBefore(LocalDateTime.now());
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public static class Builder {
        private UUID id;
        private String token;
        private Member member;
        private LocalDateTime expiryDate;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public Builder member(Member member) {
            this.member = member;
            return this;
        }

        public Builder expiryDate(LocalDateTime expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        public PasswordResetToken build() {
            return new PasswordResetToken(id, token, member, expiryDate);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}