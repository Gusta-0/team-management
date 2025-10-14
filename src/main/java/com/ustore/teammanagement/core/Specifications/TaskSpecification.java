package com.ustore.teammanagement.core.Specifications;

import com.ustore.teammanagement.core.entity.Task;
import com.ustore.teammanagement.enums.Priority;
import com.ustore.teammanagement.enums.TaskStatus;
import com.ustore.teammanagement.payload.dto.request.TaskFilterRequest;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.UUID;

public class TaskSpecification {

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

    public static Specification<Task> withAssignee(UUID assigneeId) {
        return (root, query, cb) ->
                assigneeId == null ? null : cb.equal(root.join("assignee", JoinType.LEFT).get("id"), assigneeId);
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
                return cb.between(root.get("dueDate"), from, to);
            }
            if (from != null) {
                return cb.greaterThanOrEqualTo(root.get("dueDate"), from);
            }
            if (to != null) {
                return cb.lessThanOrEqualTo(root.get("dueDate"), to);
            }
            return null;
        };
    }

    public static Specification<Task> onlyOverdue(Boolean onlyOverdue) {
        return (root, query, cb) -> {
            if (onlyOverdue == null || !onlyOverdue) return null;
            return cb.lessThan(root.get("dueDate"), LocalDate.now());
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


//    public static Specification<Task> withFilters(TaskFilterRequest filter) {
//        return Specification.allOf(
//                withAssigneeName(filter.assigneeName()),
//                withCreatedByName(filter.createdByName()),
//                withTitle(filter.title()),
//                withProject(filter.project()),
//                withStatus(filter.status()),
//                withPriority(filter.priority()),
//                withAssignee(filter.assigneeId()),
//                withDueDateRange(filter.dueDateFrom(), filter.dueDateTo()),
//                onlyOverdue(filter.onlyOverdue())
//        );
//    }
}