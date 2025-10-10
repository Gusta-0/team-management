package com.ustore.teammanagement.core.entity;

import com.ustore.teammanagement.enums.MemberStatus;
import com.ustore.teammanagement.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "members")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Member implements UserDetails {

    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
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

    @PrePersist
    public void prePersist() {
        this.joinDate = OffsetDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "ROLE_" + this.getUsername() + "_" + this.role.name());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }
}
