package com.taskflow.taskflow.task.service;

import com.taskflow.taskflow.project.model.Project;
import com.taskflow.taskflow.project.repository.ProjectMemberRepository;
import com.taskflow.taskflow.project.repository.ProjectRepository;
import com.taskflow.taskflow.security.UserPrincipal;
import com.taskflow.taskflow.task.dto.TaskRequest;
import com.taskflow.taskflow.task.dto.TaskResponse;
import com.taskflow.taskflow.task.model.Task;
import com.taskflow.taskflow.task.model.TaskStatus;
import com.taskflow.taskflow.task.repository.TaskRepository;
import com.taskflow.taskflow.user.model.UserRole;
import com.taskflow.taskflow.user.model.UserStatus;
import com.taskflow.taskflow.user.model.Usuario;
import com.taskflow.taskflow.user.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @InjectMocks
    private TaskService taskService;

    private UUID projectId;
    private UUID userId;
    private UUID assigneeId;
    private UserPrincipal currentUser;
    private TaskRequest taskRequest;
    private Project project;
    private Usuario assignee;

    @BeforeEach
    void setUp() {
        projectId = UUID.randomUUID();
        userId = UUID.randomUUID();
        assigneeId = UUID.randomUUID();

        currentUser = new UserPrincipal(new Usuario(userId, "Test User", "test@example.com", "password", UserRole.COLLABORATOR, UserStatus.ACTIVE, Instant.now()));
        taskRequest = new TaskRequest("Task Title", "Task Description", assigneeId, Instant.now().plusSeconds(3600));

        project = new Project();
        project.setId(projectId);
        project.setName("Test Project");

        assignee = new Usuario();
        assignee.setId(assigneeId);
        assignee.setName("Assignee User");
        assignee.setEmail("assignee@example.com");
    }

    @Test
    @DisplayName("Deve criar uma tarefa com sucesso quando o usuário é membro do projeto")
    void createTask_ShouldCreateTaskSuccessfully_WhenUserIsProjectMember() {
        // Arrange
        when(projectMemberRepository.existsByProjectIdAndUserId(projectId, userId)).thenReturn(true);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(usuarioRepository.findById(assigneeId)).thenReturn(Optional.of(assignee));

        Task savedTask = new Task();
        savedTask.setId(UUID.randomUUID());
        savedTask.setTitle(taskRequest.title());
        savedTask.setDescription(taskRequest.description());
        savedTask.setProject(project);
        savedTask.setAssignee(assignee);
        savedTask.setDueDate(taskRequest.dueDate());
        savedTask.setStatus(TaskStatus.TODO);
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        // Act
        TaskResponse response = taskService.createTask(projectId, taskRequest, currentUser);

        // Assert
        assertNotNull(response);
        assertEquals(savedTask.getId(), response.id());
        assertEquals(taskRequest.title(), response.title());
        assertEquals(TaskStatus.TODO, response.status());
        assertEquals(projectId, response.projectId());
        assertEquals(assigneeId, response.assigneeId());

        verify(projectMemberRepository, times(1)).existsByProjectIdAndUserId(projectId, userId);
        verify(projectRepository, times(1)).findById(projectId);
        verify(usuarioRepository, times(1)).findById(assigneeId);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException quando o usuário não é membro do projeto")
    void createTask_ShouldThrowAccessDeniedException_WhenUserIsNotProjectMember() {
        // Arrange
        when(projectMemberRepository.existsByProjectIdAndUserId(projectId, userId)).thenReturn(false);

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> {
            taskService.createTask(projectId, taskRequest, currentUser);
        });

        verify(projectMemberRepository, times(1)).existsByProjectIdAndUserId(projectId, userId);
        verify(projectRepository, never()).findById(any(UUID.class));
        verify(usuarioRepository, never()).findById(any(UUID.class));
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("Deve lançar RuntimeException quando o projeto não é encontrado")
    void createTask_ShouldThrowRuntimeException_WhenProjectNotFound() {
        // Arrange
        when(projectMemberRepository.existsByProjectIdAndUserId(projectId, userId)).thenReturn(true);
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            taskService.createTask(projectId, taskRequest, currentUser);
        }, "Project not found");

        verify(projectMemberRepository, times(1)).existsByProjectIdAndUserId(projectId, userId);
        verify(projectRepository, times(1)).findById(projectId);
        verify(usuarioRepository, never()).findById(any(UUID.class));
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("Deve lançar RuntimeException quando o responsável (assignee) não é encontrado")
    void createTask_ShouldThrowRuntimeException_WhenAssigneeNotFound() {
        // Arrange
        when(projectMemberRepository.existsByProjectIdAndUserId(projectId, userId)).thenReturn(true);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(usuarioRepository.findById(assigneeId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            taskService.createTask(projectId, taskRequest, currentUser);
        }, "Assignee not found");

        verify(projectMemberRepository, times(1)).existsByProjectIdAndUserId(projectId, userId);
        verify(projectRepository, times(1)).findById(projectId);
        verify(usuarioRepository, times(1)).findById(assigneeId);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("Deve excluir a tarefa com sucesso quando o usuário é membro do projeto")
    void deleteTask_ShouldSucceed_WhenUserIsProjectMember() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        Project project = new Project();
        project.setId(projectId);

        Task existingTask = new Task();
        existingTask.setId(taskId);
        existingTask.setProject(project);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(projectMemberRepository.existsByProjectIdAndUserId(projectId, userId)).thenReturn(true);
        doNothing().when(taskRepository).deleteById(taskId);

        // Act
        assertDoesNotThrow(() -> taskService.deleteTask(taskId, currentUser));

        // Assert
        verify(taskRepository, times(1)).deleteById(taskId);
    }

    @Test
    @DisplayName("Deve filtrar tarefas por status e responsável")
    void getTasksByProject_ShouldFilterTasks() {
        // Arrange
        when(projectMemberRepository.existsByProjectIdAndUserId(projectId, userId)).thenReturn(true);

        Task task1 = new Task();
        task1.setId(UUID.randomUUID());
        task1.setTitle("Task 1");
        task1.setStatus(TaskStatus.IN_PROGRESS);
        task1.setAssignee(assignee);
        task1.setProject(project);

        when(taskRepository.findTasksByProjectIdAndFilters(projectId, TaskStatus.IN_PROGRESS, assigneeId))
                .thenReturn(java.util.List.of(task1));

        // Act
        java.util.List<TaskResponse> responses = taskService.getTasksByProject(projectId, "IN_PROGRESS", assigneeId, currentUser);

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(task1.getId(), responses.get(0).id());

        verify(projectMemberRepository, times(1)).existsByProjectIdAndUserId(projectId, userId);
        verify(taskRepository, times(1)).findTasksByProjectIdAndFilters(projectId, TaskStatus.IN_PROGRESS, assigneeId);
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException ao excluir tarefa se o usuário não for membro")
    void deleteTask_ShouldThrowAccessDeniedException_WhenUserIsNotProjectMember() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        Project project = new Project();
        project.setId(projectId);

        Task existingTask = new Task();
        existingTask.setId(taskId);
        existingTask.setProject(project);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(projectMemberRepository.existsByProjectIdAndUserId(projectId, userId)).thenReturn(false);

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> {
            taskService.deleteTask(taskId, currentUser);
        });

        verify(taskRepository, never()).deleteById(any(UUID.class));
    }
}
