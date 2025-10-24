package com.ustore.teammanagement.core.repository;

import com.ustore.teammanagement.core.entity.Task;
import com.ustore.teammanagement.core.enums.TaskStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID>, JpaSpecificationExecutor<Task> {
    long countByStatusNot(TaskStatus status);
    long countByStatusIn(List<TaskStatus> statuses);
    List<Task> findTop5ByOrderByUpdatedAtDesc();

    @Query("""
        SELECT t FROM Task t
        WHERE t.status IN ('TO_DO', 'COMPLETED')
        ORDER BY COALESCE(t.updatedAt, t.createdAt) DESC
        """)
    List<Task> findTop5RecentTasks(Pageable pageable);

    List<Task> findByStatusInAndDueDateBefore(List<TaskStatus> statuses, LocalDate date);

    @Query(value = """
    SELECT
        DATE_TRUNC('day', t.created_at) AS date,
        COUNT(*) FILTER (WHERE t.status IN ('TO_DO', 'IN_PROGRESS', 'REVISION', 'LATE')) AS created,
        COUNT(*) FILTER (WHERE t.status = 'COMPLETED') AS completed
    FROM tasks t
    WHERE t.created_at >= NOW() - make_interval(days => :days)
    GROUP BY DATE_TRUNC('day', t.created_at)
    ORDER BY date
""", nativeQuery = true)
    List<Object[]> findTaskTrend(@Param("days") int days);




    @Query(value = """
    SELECT
        m.department AS department,
        COUNT(*) FILTER (WHERE t.status = 'COMPLETED') AS completed,
        COUNT(*) FILTER (WHERE t.status != 'COMPLETED' OR t.status IS NULL) AS pending
    FROM members m
    LEFT JOIN tasks t ON t.assignee_id = m.id
    WHERE m.department IS NOT NULL
    GROUP BY m.department
    ORDER BY m.department
""", nativeQuery = true)
    List<Object[]> findDepartmentPerformance();


}
