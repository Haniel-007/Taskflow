package com.taskflow.taskflow.task.dto;

import com.taskflow.taskflow.task.model.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateTaskStatusRequest(
    @NotNull
    TaskStatus status
) {}
