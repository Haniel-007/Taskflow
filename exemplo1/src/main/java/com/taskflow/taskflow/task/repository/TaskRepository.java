package com.taskflow.taskflow.task.repository;

import com.taskflow.taskflow.task.model.Task;
import com.taskflow.taskflow.task.model.TaskStatus;
import com.taskflow.taskflow.user.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findAllByProjectId(UUID projectId);

    long countByStatusAndAssignee(TaskStatus status, Usuario assignee);

    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId " +
            "AND (:status IS NULL OR t.status = :status) " +
            "AND (:assigneeId IS NULL OR t.assignee.id = :assigneeId)")
    List<Task> findTasksByProjectIdAndFilters(
            @Param("projectId") UUID projectId,
            @Param("status") TaskStatus status,
            @Param("assigneeId") UUID assigneeId
    );
}
