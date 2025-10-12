package com.ustore.teammanagement.core.Specifications;

import com.ustore.teammanagement.core.entity.Task;
import com.ustore.teammanagement.enums.Priority;
import com.ustore.teammanagement.enums.TaskStatus;
import com.ustore.teammanagement.payload.dto.request.TaskFilterRequest;
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
                assigneeId == null ? null : cb.equal(root.join("assignee").get("id"), assigneeId);
    }

    public static Specification<Task> withProject(String project) {
        return (root, query, cb) -> {
            if (project == null || project.isBlank()) return null;
            String like = "%" + project.toLowerCase() + "%";
            return cb.like(cb.lower(root.get("project")), like);
        };
    }

    public static Specification<Task> withTag(String tag) {
        return (root, query, cb) -> {
            if (tag == null || tag.isBlank()) return null;
            String like = "%" + tag.toLowerCase() + "%";
            // Assuming tags is a collection of strings
            return cb.isMember(tag.toLowerCase(), root.get("tags"));
        };
    }

    public static Specification<Task> withAssigneeName(String assigneeName) {
        return (root, query, cb) -> {
            if (assigneeName == null || assigneeName.isBlank()) return null;
            String like = "%" + assigneeName.toLowerCase() + "%";
            return cb.like(cb.lower(root.join("assignee").get("name")), like);
        };
    }

    public static Specification<Task> withCreatedByName(String createdByName) {
        return (root, query, cb) -> {
            if (createdByName == null || createdByName.isBlank()) return null;
            String like = "%" + createdByName.toLowerCase() + "%";
            return cb.like(cb.lower(root.join("createdBy").get("name")), like);
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

    public static Specification<Task> withFilters(TaskFilterRequest filter) {
        return Specification.allOf(
                withAssigneeName(filter.assigneeName()),
                withCreatedByName(filter.createdByName()),
                withTitle(filter.title()),
                withProject(filter.project()),
                withTag(filter.tag()),
                withStatus(filter.status()),
                withPriority(filter.priority()),
                withAssignee(filter.assigneeId()),
                withDueDateRange(filter.dueDateFrom(), filter.dueDateTo()),
                onlyOverdue(filter.onlyOverdue())
        );
    }
}

