package com.ustore.teammanagement.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ustore.teammanagement.core.enums.MemberStatus;
import com.ustore.teammanagement.core.enums.Role;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "members")
public class Member implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String email;
    @JsonIgnore
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String department;
    private String phone;
    private OffsetDateTime joinDate;
    @Enumerated(EnumType.STRING)
    private MemberStatus status = MemberStatus.ACTIVE;
    private String image;

    @Column(name = "failed_attempts")
    private Integer failedAttempts = 0;

    @Column(name = "account_locked")
    private Boolean accountLocked = false;

    @Column(name = "lock_time")
    private LocalDateTime lockTime;

    public Member() {}

    public Member(UUID id, String name, String email, String password, Role role,
                  String department, String phone, OffsetDateTime joinDate,
                  MemberStatus status, String image, Integer failedAttempts,
                  Boolean accountLocked, LocalDateTime lockTime) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.department = department;
        this.phone = phone;
        this.joinDate = joinDate;
        this.status = status;
        this.image = image;
        this.failedAttempts = failedAttempts;
        this.accountLocked = accountLocked;
        this.lockTime = lockTime;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public OffsetDateTime getJoinDate() { return joinDate; }
    public void setJoinDate(OffsetDateTime joinDate) { this.joinDate = joinDate; }

    public MemberStatus getStatus() { return status; }
    public void setStatus(MemberStatus status) { this.status = status; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public Integer getFailedAttempts() { return failedAttempts; }
    public void setFailedAttempts(Integer failedAttempts) { this.failedAttempts = failedAttempts; }

    public Boolean getAccountLocked() { return accountLocked; }
    public void setAccountLocked(Boolean accountLocked) { this.accountLocked = accountLocked; }

    public LocalDateTime getLockTime() { return lockTime; }
    public void setLockTime(LocalDateTime lockTime) { this.lockTime = lockTime; }

    @PrePersist
    public void prePersist() {
        this.joinDate = OffsetDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "ROLE_" + this.getUsername() + "_" + this.role.name());
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return !accountLocked; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return this.status == MemberStatus.ACTIVE; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Member)) return false;
        Member member = (Member) o;
        return Objects.equals(id, member.id) &&
                Objects.equals(email, member.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", department='" + department + '\'' +
                ", status=" + status +
                '}';
    }

    public static class Builder {
        private UUID id;
        private String name;
        private String email;
        private String password;
        private Role role;
        private String department;
        private String phone;
        private OffsetDateTime joinDate;
        private MemberStatus status = MemberStatus.ACTIVE;
        private String image;
        private Integer failedAttempts = 0;
        private Boolean accountLocked = false;
        private LocalDateTime lockTime;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder password(String password) { this.password = password; return this; }
        public Builder role(Role role) { this.role = role; return this; }
        public Builder department(String department) { this.department = department; return this; }
        public Builder phone(String phone) { this.phone = phone; return this; }
        public Builder joinDate(OffsetDateTime joinDate) { this.joinDate = joinDate; return this; }
        public Builder status(MemberStatus status) { this.status = status; return this; }
        public Builder image(String image) { this.image = image; return this; }
        public Builder failedAttempts(Integer failedAttempts) { this.failedAttempts = failedAttempts; return this; }
        public Builder accountLocked(Boolean accountLocked) { this.accountLocked = accountLocked; return this; }
        public Builder lockTime(LocalDateTime lockTime) { this.lockTime = lockTime; return this; }

        public Member build() {
            return new Member(id, name, email, password, role, department, phone, joinDate, status, image, failedAttempts, accountLocked, lockTime);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
