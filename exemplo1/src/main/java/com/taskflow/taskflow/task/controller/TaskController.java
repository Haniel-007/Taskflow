package com.taskflow.taskflow.task.controller;

import com.taskflow.taskflow.security.UserPrincipal;
import com.taskflow.taskflow.task.dto.TaskRequest;
import com.taskflow.taskflow.task.dto.TaskResponse;
import com.taskflow.taskflow.task.service.TaskService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import com.taskflow.taskflow.task.dto.UpdateTaskStatusRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    @Autowired
    TaskService taskService;

    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<TaskResponse> createTask(
            @PathVariable UUID projectId,
            @RequestBody TaskRequest taskRequest,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        TaskResponse createdTask = taskService.createTask(projectId, taskRequest, currentUser);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<List<TaskResponse>> getTasksByProject(
            @PathVariable UUID projectId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID assigneeId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        List<TaskResponse> tasks = taskService.getTasksByProject(projectId, status, assigneeId, currentUser);
        return ResponseEntity.ok(tasks);
    }

    @PatchMapping("/tasks/{taskId}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(
            @PathVariable UUID taskId,
            @Valid @RequestBody UpdateTaskStatusRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        TaskResponse updatedTask = taskService.updateTaskStatus(taskId, request.status(), currentUser);
        return ResponseEntity.ok(updatedTask);
    }

    @PutMapping("/tasks/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable UUID taskId,
            @Valid @RequestBody TaskRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        TaskResponse updatedTask = taskService.updateTask(taskId, request, currentUser);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable UUID taskId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        taskService.deleteTask(taskId, currentUser);
        return ResponseEntity.noContent().build();
    }
}
