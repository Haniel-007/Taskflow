package com.taskflow.taskflow.project.controller;

import com.taskflow.taskflow.project.dto.ProjectRequest;
import com.taskflow.taskflow.project.dto.ProjectResponse;
import com.taskflow.taskflow.project.service.ProjectService;
import com.taskflow.taskflow.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.taskflow.taskflow.project.dto.AddMemberRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    @Autowired
    ProjectService projectService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ProjectResponse> createProject(
            @Valid @RequestBody ProjectRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        ProjectResponse response = projectService.createProject(request, currentUser.getUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<java.util.List<ProjectResponse>> getProjectsByUser(@AuthenticationPrincipal UserPrincipal currentUser) {
        java.util.List<ProjectResponse> projects = projectService.getProjectsByUser(currentUser);
        return ResponseEntity.ok(projects);
    }

    @PostMapping("/{projectId}/members")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> addMemberToProject(
            @PathVariable UUID projectId,
            @Valid @RequestBody AddMemberRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        projectService.addMemberToProject(projectId, request, currentUser);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}
