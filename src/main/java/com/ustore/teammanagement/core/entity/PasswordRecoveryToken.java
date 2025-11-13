package com.ustore.teammanagement.core.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class PasswordRecoveryToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member member;

    @Column(nullable = false)
    private LocalDateTime expiration;

    private boolean used = false;

    public PasswordRecoveryToken() {
    }

    public PasswordRecoveryToken(String token, Member member, LocalDateTime expiration) {
        this.token = token;
        this.member = member;
        this.expiration = expiration;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public LocalDateTime getExpiration() {
        return expiration;
    }

    public void setExpiration(LocalDateTime expiration) {
        this.expiration = expiration;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}
