package com.ustore.teammanagement.core.entity;

import com.ustore.teammanagement.enums.Department;
import com.ustore.teammanagement.enums.Priority;
import com.ustore.teammanagement.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {
    @Id @GeneratedValue (strategy = GenerationType.UUID)
    private Long id;
    private String title;
    @Column(length = 4000)
    private String description;
    @Enumerated(EnumType.STRING) private TaskStatus status;
    @Enumerated(EnumType.STRING) private Priority priority;
    @Enumerated(EnumType.STRING) private Department department;
    private LocalDate dueDate;
    private String project;
    @ElementCollection
    private List<String> tags;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private User createdBy;
}
