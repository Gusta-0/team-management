package com.ustore.teammanagement.core.repository;

import com.ustore.teammanagement.core.entity.Task;
import com.ustore.teammanagement.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID>, JpaSpecificationExecutor<Task> {
    long countByStatus(TaskStatus status);

    long countByStatusIn(List<TaskStatus> statuses);
}
