# Casos de Teste - Funcionalidades de Tarefas (Tasks)

Este documento detalha os casos de teste para as funcionalidades de **Criação, Visualização, Atualização de Status, Edição, Exclusão e Filtragem de Tarefas**.

## 1. Testes de Unidade (JUnit 5 & Mockito)

### Código do Teste (`/src/test/java/com/taskflow/taskflow/task/service/TaskServiceTest.java`)

```java
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock private TaskRepository taskRepository;
    @Mock private ProjectRepository projectRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private ProjectMemberRepository projectMemberRepository;

    @InjectMocks private TaskService taskService;

    private UUID projectId;
    private UUID userId;
    private UUID assigneeId;
    private UserPrincipal currentUser;
    private Project project;
    private Usuario assignee;

    @BeforeEach
    void setUp() {
        projectId = UUID.randomUUID();
        userId = UUID.randomUUID();
        assigneeId = UUID.randomUUID();

        currentUser = new UserPrincipal(new Usuario(userId, "Test User", "test@example.com", "password", UserRole.COLLABORATOR, UserStatus.ACTIVE, Instant.now()));
        
        project = new Project();
        project.setId(projectId);
        project.setName("Test Project");

        assignee = new Usuario();
        assignee.setId(assigneeId);
        assignee.setName("Assignee User");
    }

    // --- Testes para Criação de Tarefa (GT-1) ---
    @Test
    @DisplayName("Deve criar uma tarefa com sucesso quando o usuário é membro do projeto")
    void createTask_ShouldCreateTaskSuccessfully_WhenUserIsProjectMember() {
        // Arrange
        TaskRequest taskRequest = new TaskRequest("Task Title", "Task Description", assigneeId, Instant.now());
        when(projectMemberRepository.existsByProjectIdAndUserId(projectId, userId)).thenReturn(true);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(usuarioRepository.findById(assigneeId)).thenReturn(Optional.of(assignee));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TaskResponse response = taskService.createTask(projectId, taskRequest, currentUser);

        // Assert
        assertNotNull(response);
        assertEquals("Task Title", response.title());
        assertEquals(TaskStatus.TODO, response.status());
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException ao criar tarefa se o usuário não for membro")
    void createTask_ShouldThrowAccessDeniedException_WhenUserIsNotProjectMember() {
        // Arrange
        TaskRequest taskRequest = new TaskRequest("Task Title", "Task Description", assigneeId, Instant.now());
        when(projectMemberRepository.existsByProjectIdAndUserId(projectId, userId)).thenReturn(false);

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> taskService.createTask(projectId, taskRequest, currentUser));
    }

    // --- Testes para Visualização e Filtragem de Tarefas (GT-2.1, GT-4) ---
    @Test
    @DisplayName("Deve retornar a lista de tarefas quando o usuário é membro do projeto (sem filtros)")
    void getTasksByProject_ShouldReturnAllTasks_WhenUserIsProjectMemberAndNoFilters() {
        // Arrange
        when(projectMemberRepository.existsByProjectIdAndUserId(projectId, userId)).thenReturn(true);
        when(taskRepository.findTasksByProjectIdAndFilters(projectId, null, null)).thenReturn(Arrays.asList(new Task(), new Task()));

        // Act
        List<TaskResponse> tasks = taskService.getTasksByProject(projectId, null, null, currentUser);

        // Assert
        assertNotNull(tasks);
        assertEquals(2, tasks.size());
    }
    
    @Test
    @DisplayName("Deve filtrar tarefas por status e responsável")
    void getTasksByProject_ShouldFilterTasks() {
        // Arrange
        when(projectMemberRepository.existsByProjectIdAndUserId(projectId, userId)).thenReturn(true);
        Task task = new Task();
        task.setId(UUID.randomUUID());
        task.setProject(project);
        task.setAssignee(assignee);
        when(taskRepository.findTasksByProjectIdAndFilters(projectId, TaskStatus.IN_PROGRESS, assigneeId)).thenReturn(List.of(task));

        // Act
        List<TaskResponse> responses = taskService.getTasksByProject(projectId, "IN_PROGRESS", assigneeId, currentUser);

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException ao buscar tarefas se o usuário não for membro")
    void getTasksByProject_ShouldThrowAccessDeniedException_WhenUserIsNotProjectMember() {
        // Arrange
        when(projectMemberRepository.existsByProjectIdAndUserId(projectId, userId)).thenReturn(false);

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> taskService.getTasksByProject(projectId, null, null, currentUser));
    }

    // --- Testes para Atualização de Status (GT-3) ---
    @Test
    @DisplayName("Deve atualizar o status da tarefa com sucesso quando o usuário é membro")
    void updateTaskStatus_ShouldSucceed_WhenUserIsProjectMember() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        Task task = new Task();
        task.setId(taskId);
        task.setProject(project);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(projectMemberRepository.existsByProjectIdAndUserId(projectId, userId)).thenReturn(true);
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TaskResponse response = taskService.updateTaskStatus(taskId, TaskStatus.DONE, currentUser);

        // Assert
        assertNotNull(response);
        assertEquals(TaskStatus.DONE, response.status());
    }

    // --- Testes para Edição de Tarefa (GT-4 Story) ---
    @Test
    @DisplayName("Deve editar a tarefa com sucesso quando o usuário é membro do projeto")
    void updateTask_ShouldSucceed_WhenUserIsProjectMember() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        TaskRequest updateRequest = new TaskRequest("Updated Title", "Updated Desc", assigneeId, Instant.now());
        Task existingTask = new Task();
        existingTask.setId(taskId);
        existingTask.setProject(project);
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(projectMemberRepository.existsByProjectIdAndUserId(projectId, userId)).thenReturn(true);
        when(usuarioRepository.findById(assigneeId)).thenReturn(Optional.of(assignee));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TaskResponse response = taskService.updateTask(taskId, updateRequest, currentUser);

        // Assert
        assertNotNull(response);
        assertEquals("Updated Title", response.title());
    }

    // --- Testes para Exclusão de Tarefa (GT-5) ---
    @Test
    @DisplayName("Deve excluir a tarefa com sucesso quando o usuário é membro do projeto")
    void deleteTask_ShouldSucceed_WhenUserIsProjectMember() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        Task existingTask = new Task();
        existingTask.setId(taskId);
        existingTask.setProject(project);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(projectMemberRepository.existsByProjectIdAndUserId(projectId, userId)).thenReturn(true);
        doNothing().when(taskRepository).deleteById(taskId);

        // Act & Assert
        assertDoesNotThrow(() -> taskService.deleteTask(taskId, currentUser));
        verify(taskRepository, times(1)).deleteById(taskId);
    }
}
```

## 2. Validação Manual (API Endpoint - Postman)

**URL Base:** `http://localhost:8080`

### Pré-requisitos Gerais:
- A aplicação precisa estar em execução.
- Tenha tokens de autenticação JWT para usuários que são membros de um projeto e para usuários que não são.
- Anote os IDs de projetos, tarefas e usuários para usar nas requisições.

---

### Seção 2.1: Criação de Tarefas (`POST /api/projects/{projectId}/tasks`)

#### Caso de Teste 2.1.1: Criação com Sucesso
-   **Objetivo:** Verificar se um membro do projeto consegue criar uma tarefa.
-   **Endpoint:** `POST /api/projects/{projectId}/tasks`
-   **Token:** Membro do Projeto
-   **Body:**
    ```json
    {
      "title": "Implementar Feature Y",
      "description": "Descrição da nova funcionalidade.",
      "assigneeId": "{userId}",
      "dueDate": "2026-01-01T23:59:59Z"
    }
    ```
-   **Resultado Esperado:** Status `201 Created` com os dados da tarefa criada.

#### Caso de Teste 2.1.2: Falha por Não Ser Membro
-   **Objetivo:** Verificar se um usuário que não é membro do projeto é proibido de criar tarefas.
-   **Endpoint:** `POST /api/projects/{projectId}/tasks`
-   **Token:** **NÃO** Membro do Projeto
-   **Resultado Esperado:** Status `403 Forbidden`.

---

### Seção 2.2: Visualização e Filtragem de Tarefas (`GET /api/projects/{projectId}/tasks`)

#### Caso de Teste 2.2.1: Listar Todas as Tarefas (Sem Filtro)
-   **Objetivo:** Verificar se um membro do projeto consegue listar todas as tarefas.
-   **Endpoint:** `GET /api/projects/{projectId}/tasks`
-   **Token:** Membro do Projeto
-   **Resultado Esperado:** Status `200 OK` com a lista de todas as tarefas do projeto.

#### Caso de Teste 2.2.2: Filtrar por Status
-   **Objetivo:** Verificar se a lista de tarefas é filtrada corretamente pelo status.
-   **Endpoint:** `GET /api/projects/{projectId}/tasks?status=IN_PROGRESS`
-   **Token:** Membro do Projeto
-   **Resultado Esperado:** Status `200 OK` com uma lista contendo apenas tarefas com o status `IN_PROGRESS`.

#### Caso de Teste 2.2.3: Filtrar por Responsável
-   **Objetivo:** Verificar se a lista de tarefas é filtrada pelo ID do responsável.
-   **Endpoint:** `GET /api/projects/{projectId}/tasks?assigneeId={userId}`
-   **Token:** Membro do Projeto
-   **Resultado Esperado:** Status `200 OK` com uma lista contendo apenas tarefas atribuídas ao usuário especificado.

#### Caso de Teste 2.2.4: Filtrar por Status e Responsável
-   **Objetivo:** Verificar se a filtragem combinada funciona.
-   **Endpoint:** `GET /api/projects/{projectId}/tasks?status=DONE&assigneeId={userId}`
-   **Token:** Membro do Projeto
-   **Resultado Esperado:** Status `200 OK` com uma lista de tarefas que atendem a ambos os critérios.

#### Caso de Teste 2.2.5: Falha por Não Ser Membro
-   **Objetivo:** Verificar se um não-membro é proibido de listar tarefas.
-   **Endpoint:** `GET /api/projects/{projectId}/tasks`
-   **Token:** **NÃO** Membro do Projeto
-   **Resultado Esperado:** Status `403 Forbidden`.

---

### Seção 2.3: Atualização de Status (`PATCH /api/tasks/{taskId}/status`)

#### Caso de Teste 2.3.1: Atualização com Sucesso
-   **Objetivo:** Verificar se um membro do projeto consegue atualizar o status de uma tarefa.
-   **Endpoint:** `PATCH /api/tasks/{taskId}/status`
-   **Token:** Membro do Projeto
-   **Body:**
    ```json
    {
      "status": "DONE"
    }
    ```
-   **Resultado Esperado:** Status `200 OK` com os dados da tarefa atualizada.

---

### Seção 2.4: Edição de Tarefa (`PUT /api/tasks/{taskId}`)

#### Caso de Teste 2.4.1: Edição com Sucesso
-   **Objetivo:** Verificar se um membro do projeto consegue editar os detalhes de uma tarefa.
-   **Endpoint:** `PUT /api/tasks/{taskId}`
-   **Token:** Membro do Projeto
-   **Body:**
    ```json
    {
      "title": "Título Editado",
      "description": "Descrição atualizada.",
      "assigneeId": "{newUserId}",
      "dueDate": "2027-01-01T23:59:59Z"
    }
    ```
-   **Resultado Esperado:** Status `200 OK` com os dados da tarefa atualizada.

---

### Seção 2.5: Exclusão de Tarefa (`DELETE /api/tasks/{taskId}`)

#### Caso de Teste 2.5.1: Exclusão com Sucesso
-   **Objetivo:** Verificar se um membro do projeto consegue excluir uma tarefa.
-   **Endpoint:** `DELETE /api/tasks/{taskId}`
-   **Token:** Membro do Projeto
-   **Resultado Esperado:** Status `204 No Content`.
