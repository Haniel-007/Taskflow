package com.taskflow.taskflow.task.dto;

import com.taskflow.taskflow.task.model.Task;
import com.taskflow.taskflow.task.model.TaskStatus;

import java.time.Instant;
import java.util.UUID;

public record TaskResponse(
        UUID id,
        String title,
        String description,
        TaskStatus status,
        UUID projectId,
        UUID assigneeId,
        Instant dueDate,
        Instant createdAt
) {
    public static TaskResponse fromEntity(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getProject().getId(),
                task.getAssignee() != null ? task.getAssignee().getId() : null,
                task.getDueDate(),
                task.getCreatedAt()
        );
    }
}
