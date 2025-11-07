package com.taskflow.taskflow.task.dto;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TaskRequest(
        @NotBlank(message = "Title cannot be blank")
        @Size(max = 255)
        String title,
        String description,
        @NotNull(message = "Assignee ID cannot be null")
        UUID assigneeId,
        @FutureOrPresent(message = "Due date must be in the present or future")
        Instant dueDate
) {}
