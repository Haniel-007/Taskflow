package com.taskflow.taskflow.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectRequest(
    @NotBlank(message = "Project name cannot be blank")
    @Size(max = 255)
    String name,

    @Size(max = 1000)
    String description
) {}
