package com.taskflow.taskflow.project.dto;

import com.taskflow.taskflow.project.model.Project;
import java.util.UUID;

public record ProjectResponse(
    UUID id,
    String name,
    String description,
    UUID ownerId
) {
    public static ProjectResponse fromEntity(Project project) {
        return new ProjectResponse(
            project.getId(),
            project.getName(),
            project.getDescription(),
            project.getOwner().getId()
        );
    }
}
