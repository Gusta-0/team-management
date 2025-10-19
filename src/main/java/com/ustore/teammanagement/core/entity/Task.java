package com.ustore.teammanagement.core.entity;

import com.ustore.teammanagement.enums.Priority;
import com.ustore.teammanagement.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {
    @Id @GeneratedValue (strategy = GenerationType.UUID)
    private UUID id;
    private String title;
    @Column(length = 4000)
    private String description;
    @Enumerated(EnumType.STRING) private TaskStatus status;
    @Enumerated(EnumType.STRING) private Priority priority;
    private LocalDate dueDate;
    private String project;
    @ElementCollection
    private List<String> tags;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private Member assignee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private Member createdBy;

    @PrePersist
    public void onCreate() {
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}
