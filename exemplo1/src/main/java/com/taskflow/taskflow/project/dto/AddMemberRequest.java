package com.taskflow.taskflow.project.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddMemberRequest(
        @NotNull(message = "User ID cannot be null")
        UUID userId
) {
}
