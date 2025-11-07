package com.taskflow.taskflow.task.service;

import com.taskflow.taskflow.core.exception.ResourceNotFoundException;
import com.taskflow.taskflow.project.repository.ProjectMemberRepository;
import com.taskflow.taskflow.project.repository.ProjectRepository;
import com.taskflow.taskflow.security.UserPrincipal;
import com.taskflow.taskflow.task.dto.TaskRequest;
import com.taskflow.taskflow.task.dto.TaskResponse;
import com.taskflow.taskflow.task.model.Task;
import com.taskflow.taskflow.task.model.TaskStatus;
import com.taskflow.taskflow.task.repository.TaskRepository;
import com.taskflow.taskflow.user.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProjectMemberRepository projectMemberRepository;

    @Transactional
    public TaskResponse createTask(UUID projectId, TaskRequest request, UserPrincipal currentUser) {
        // 1. Verificar se o usuário é membro do projeto
        boolean isMember = projectMemberRepository.existsByProjectIdAndUserId(projectId, currentUser.getId());
        if (!isMember) {
            throw new AccessDeniedException("User is not a member of the project");
        }

        // 2. Buscar as entidades relacionadas
        var project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        var assignee = usuarioRepository.findById(request.assigneeId())
                .orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));

        // 3. Criar e salvar a nova tarefa
        Task newTask = new Task();
        newTask.setTitle(request.title());
        newTask.setDescription(request.description());
        newTask.setProject(project);
        newTask.setAssignee(assignee);
        newTask.setDueDate(request.dueDate());
        newTask.setStatus(TaskStatus.TODO); // Status padrão

        Task savedTask = taskRepository.save(newTask);

        return TaskResponse.fromEntity(savedTask);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByProject(UUID projectId, String status, UUID assigneeId, UserPrincipal currentUser) {
        // 1. Verificar se o usuário é membro do projeto
        boolean isMember = projectMemberRepository.existsByProjectIdAndUserId(projectId, currentUser.getId());
        if (!isMember) {
            throw new AccessDeniedException("User is not a member of the project");
        }

        // 2. Converter status de String para Enum
        TaskStatus taskStatus = null;
        if (status != null && !status.isBlank()) {
            try {
                taskStatus = TaskStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Lançar uma exceção customizada ou de negócio seria o ideal
                throw new IllegalArgumentException("Invalid status value: " + status);
            }
        }

        // 3. Buscar as tarefas com filtros
        List<Task> tasks = taskRepository.findTasksByProjectIdAndFilters(projectId, taskStatus, assigneeId);

        // 4. Mapear para DTOs de resposta
        return tasks.stream()
                .map(TaskResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public TaskResponse updateTaskStatus(UUID taskId, TaskStatus newStatus, UserPrincipal currentUser) {
        // 1. Buscar a tarefa
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        // 2. Verificar se o usuário é membro do projeto da tarefa
        UUID projectId = task.getProject().getId();
        boolean isMember = projectMemberRepository.existsByProjectIdAndUserId(projectId, currentUser.getId());
        if (!isMember) {
            throw new AccessDeniedException("User is not a member of the project");
        }

        // 3. Atualizar o status e salvar
        task.setStatus(newStatus);
        Task updatedTask = taskRepository.save(task);

        return TaskResponse.fromEntity(updatedTask);
    }

    @Transactional
    public TaskResponse updateTask(UUID taskId, TaskRequest request, UserPrincipal currentUser) {
        // 1. Buscar a tarefa
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        // 2. Verificar se o usuário é membro do projeto da tarefa
        UUID projectId = task.getProject().getId();
        boolean isMember = projectMemberRepository.existsByProjectIdAndUserId(projectId, currentUser.getId());
        if (!isMember) {
            throw new AccessDeniedException("User is not a member of the project");
        }

        // 3. Buscar o novo responsável (assignee)
        var assignee = usuarioRepository.findById(request.assigneeId())
                .orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));

        // 4. Atualizar os campos da tarefa
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setDueDate(request.dueDate());
        task.setAssignee(assignee);

        Task updatedTask = taskRepository.save(task);

        return TaskResponse.fromEntity(updatedTask);
    }

    @Transactional
    public void deleteTask(UUID taskId, UserPrincipal currentUser) {
        // 1. Buscar a tarefa
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        // 2. Verificar se o usuário é membro do projeto da tarefa
        UUID projectId = task.getProject().getId();
        boolean isMember = projectMemberRepository.existsByProjectIdAndUserId(projectId, currentUser.getId());
        if (!isMember) {
            throw new AccessDeniedException("User is not a member of the project");
        }

        // 3. Excluir a tarefa
        taskRepository.deleteById(taskId);
    }
}
