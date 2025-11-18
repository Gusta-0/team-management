package com.ustore.teammanagement.core.Specifications;

import com.ustore.teammanagement.core.entity.Task;
import com.ustore.teammanagement.core.enums.Priority;
import com.ustore.teammanagement.core.enums.TaskStatus;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class TaskSpecification {

    private TaskSpecification() {
        throw new UnsupportedOperationException("Classe utilitária, não instancie.");
    }

    private static final String DUE_DATE = "dueDate";

    public static Specification<Task> withTitle(String title) {
        return (root, query, cb) -> {
            if (title == null || title.isBlank()) return null;
            String like = "%" + title.toLowerCase() + "%";
            return cb.like(cb.lower(root.get("title")), like);
        };
    }

    public static Specification<Task> withStatus(TaskStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Task> withPriority(Priority priority) {
        return (root, query, cb) ->
                priority == null ? null : cb.equal(root.get("priority"), priority);
    }

    public static Specification<Task> withProject(String project) {
        return (root, query, cb) -> {
            if (project == null || project.isBlank()) return null;
            String like = "%" + project.toLowerCase() + "%";
            return cb.like(cb.lower(root.get("project")), like);
        };
    }

    public static Specification<Task> withAssigneeName(String assigneeName) {
        return (root, query, cb) -> {
            if (assigneeName == null || assigneeName.isBlank()) return null;
            String like = "%" + assigneeName.toLowerCase() + "%";
            return cb.like(cb.lower(root.join("assignee", JoinType.LEFT).get("name")), like);
        };
    }

    public static Specification<Task> withCreatedByName(String createdByName) {
        return (root, query, cb) -> {
            if (createdByName == null || createdByName.isBlank()) return null;
            String like = "%" + createdByName.toLowerCase() + "%";
            return cb.like(cb.lower(root.join("createdBy", JoinType.LEFT).get("name")), like);
        };
    }

    public static Specification<Task> withDueDateRange(LocalDate from, LocalDate to) {
        return (root, query, cb) -> {
            if (from != null && to != null) {
                return cb.between(root.get(DUE_DATE), from, to);
            }
            if (from != null) {
                return cb.greaterThanOrEqualTo(root.get(DUE_DATE), from);
            }
            if (to != null) {
                return cb.lessThanOrEqualTo(root.get(DUE_DATE), to);
            }
            return null;
        };
    }

    public static Specification<Task> onlyOverdue(Boolean onlyOverdue) {
        return (root, query, cb) -> {
            if (onlyOverdue == null || !onlyOverdue) return null;
            return cb.lessThan(root.get(DUE_DATE), LocalDate.now());
        };
    }

    public static Specification<Task> withFilters(String title, String project, TaskStatus status, Priority priority,  String assigneeName, String createdByName, LocalDate dueDateFrom, LocalDate dueDateTo, Boolean onlyOverdue ) {
        return Specification.anyOf(
                withTitle(title),
                withProject(project),
                withStatus(status),
                withPriority(priority),
                withAssigneeName(assigneeName),
                withCreatedByName(createdByName),
                withDueDateRange(dueDateFrom, dueDateTo),
                onlyOverdue(onlyOverdue)
        );
    }
}