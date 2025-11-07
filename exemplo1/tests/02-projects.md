# Casos de Teste - Funcionalidades de Projetos

Este documento detalha os casos de teste para as funcionalidades de **Criação, Adição de Membros e Visualização de Projetos**.

## 1. Testes de Unidade (JUnit 5 & Mockito)

### Código do Teste (`/src/test/java/com/taskflow/taskflow/project/service/ProjectServiceTest.java`)

```java
package com.taskflow.taskflow.project.service;

import com.taskflow.taskflow.project.dto.ProjectRequest;
import com.taskflow.taskflow.project.dto.ProjectResponse;
import com.taskflow.taskflow.project.model.Project;
import com.taskflow.taskflow.project.model.ProjectMember;
import com.taskflow.taskflow.project.repository.ProjectMemberRepository;
import com.taskflow.taskflow.project.repository.ProjectRepository;
import com.taskflow.taskflow.user.model.UserRole;
import com.taskflow.taskflow.user.model.UserStatus;
import com.taskflow.taskflow.user.model.Usuario;
import com.taskflow.taskflow.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @Mock
    private com.taskflow.taskflow.user.repository.UsuarioRepository usuarioRepository;

    @InjectMocks
    private ProjectService projectService;

    private Usuario projectOwner;
    private ProjectRequest projectRequest;
    private UserPrincipal currentUser;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        projectOwner = new Usuario();
        projectOwner.setId(userId);
        projectOwner.setName("Sofia Manager");
        projectOwner.setEmail("sofia@example.com");
        projectOwner.setRole(UserRole.MANAGER);
        projectOwner.setStatus(UserStatus.ACTIVE);
        projectOwner.setCreatedAt(Instant.now());

        projectRequest = new ProjectRequest("Novo Projeto de Teste", "Descrição do projeto.");
        currentUser = new UserPrincipal(projectOwner);
    }

    // --- Testes para Criação de Projeto (GP-1) ---
    @Test
    @DisplayName("Deve criar um projeto e adicionar o dono como membro com sucesso")
    void createProject_ShouldCreateProjectAndAddOwnerAsMember_WhenSuccessful() {
        Project projectToSave = new Project();
        projectToSave.setName(projectRequest.name());
        projectToSave.setDescription(projectRequest.description());
        projectToSave.setOwner(projectOwner);

        Project savedProject = new Project();
        savedProject.setId(UUID.randomUUID());
        savedProject.setName(projectRequest.name());
        savedProject.setDescription(projectRequest.description());
        savedProject.setOwner(projectOwner);

        when(projectRepository.save(any(Project.class))).thenReturn(savedProject);
        when(projectMemberRepository.save(any(ProjectMember.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProjectResponse response = projectService.createProject(projectRequest, projectOwner);

        assertNotNull(response);
        assertEquals(savedProject.getId(), response.id());
        assertEquals(projectRequest.name(), response.name());
        assertEquals(projectOwner.getId(), response.ownerId());

        verify(projectRepository, times(1)).save(any(Project.class));
        verify(projectMemberRepository, times(1)).save(any(ProjectMember.class));
    }

    // --- Testes para Adição de Membros ao Projeto (GP-2) ---
    @Test
    @DisplayName("Deve adicionar um membro com sucesso quando o requisitante é o dono do projeto")
    void addMemberToProject_ShouldSucceed_WhenRequesterIsOwner() {
        UUID projectId = UUID.randomUUID();
        UUID userToAddId = UUID.randomUUID();
        UserPrincipal ownerPrincipal = new UserPrincipal(projectOwner);
        com.taskflow.taskflow.project.dto.AddMemberRequest addRequest = new com.taskflow.taskflow.project.dto.AddMemberRequest(userToAddId);

        Project project = new Project();
        project.setId(projectId);
        project.setOwner(projectOwner);

        Usuario userToAdd = new Usuario();
        userToAdd.setId(userToAddId);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(usuarioRepository.findById(userToAddId)).thenReturn(Optional.of(userToAdd));
        when(projectMemberRepository.existsByProjectIdAndUserId(projectId, userToAddId)).thenReturn(false);

        projectService.addMemberToProject(projectId, addRequest, ownerPrincipal);

        verify(projectMemberRepository, times(1)).save(any(ProjectMember.class));
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException quando o requisitante não é o dono do projeto")
    void addMemberToProject_ShouldThrowAccessDeniedException_WhenRequesterIsNotOwner() {
        UUID projectId = UUID.randomUUID();
        Usuario nonOwner = new Usuario();
        nonOwner.setId(UUID.randomUUID());
        UserPrincipal nonOwnerPrincipal = new UserPrincipal(nonOwner);
        com.taskflow.taskflow.project.dto.AddMemberRequest addRequest = new com.taskflow.taskflow.project.dto.AddMemberRequest(UUID.randomUUID());

        Project project = new Project();
        project.setId(projectId);
        project.setOwner(projectOwner);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        assertThrows(AccessDeniedException.class, () -> {
            projectService.addMemberToProject(projectId, addRequest, nonOwnerPrincipal);
        });
        verify(projectMemberRepository, never()).save(any(ProjectMember.class));
    }

    @Test
    @DisplayName("Deve lançar IllegalStateException quando o usuário já é membro do projeto")
    void addMemberToProject_ShouldThrowIllegalStateException_WhenUserIsAlreadyMember() {
        UUID projectId = UUID.randomUUID();
        UUID userToAddId = UUID.randomUUID();
        UserPrincipal ownerPrincipal = new UserPrincipal(projectOwner);
        com.taskflow.taskflow.project.dto.AddMemberRequest addRequest = new com.taskflow.taskflow.project.dto.AddMemberRequest(userToAddId);

        Project project = new Project();
        project.setId(projectId);
        project.setOwner(projectOwner);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(usuarioRepository.findById(userToAddId)).thenReturn(Optional.of(new Usuario()));
        when(projectMemberRepository.existsByProjectIdAndUserId(projectId, userToAddId)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> {
            projectService.addMemberToProject(projectId, addRequest, ownerPrincipal);
        });
        verify(projectMemberRepository, never()).save(any(ProjectMember.class));
    }

    // --- Testes para Visualização de Meus Projetos (GP-3) ---
    @Test
    @DisplayName("Deve retornar a lista de projetos quando o usuário é membro")
    void getProjectsByUser_ShouldReturnProjects_WhenUserIsMember() {
        Project project1 = new Project();
        project1.setId(UUID.randomUUID());
        project1.setName("Project Alpha");
        project1.setOwner(projectOwner);

        Project project2 = new Project();
        project2.setId(UUID.randomUUID());
        project2.setName("Project Beta");
        project2.setOwner(projectOwner);

        ProjectMember member1 = new ProjectMember(project1, projectOwner);
        ProjectMember member2 = new ProjectMember(project2, projectOwner);

        when(projectMemberRepository.findAllByUserId(userId)).thenReturn(List.of(member1, member2));

        List<ProjectResponse> projects = projectService.getProjectsByUser(currentUser);

        assertNotNull(projects);
        assertEquals(2, projects.size());
        assertEquals("Project Alpha", projects.get(0).name());
        assertEquals("Project Beta", projects.get(1).name());
        verify(projectMemberRepository, times(1)).findAllByUserId(userId);
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia quando o usuário não é membro de nenhum projeto")
    void getProjectsByUser_ShouldReturnEmptyList_WhenUserIsNotMember() {
        when(projectMemberRepository.findAllByUserId(userId)).thenReturn(new ArrayList<>());

        List<ProjectResponse> projects = projectService.getProjectsByUser(currentUser);

        assertNotNull(projects);
        assertTrue(projects.isEmpty());
        verify(projectMemberRepository, times(1)).findAllByUserId(userId);
    }
}
```

## 2. Validação Manual (API Endpoint - Postman)

**URL Base:** `http://localhost:8080`

### Pré-requisitos Gerais:
- A aplicação precisa estar em execução.
- Tenha tokens de autenticação JWT para usuários com diferentes roles (MANAGER, COLLABORATOR).
- Anote os IDs de projetos, tarefas e usuários para usar nas requisições.

---

### Seção 2.1: Criação de um Novo Projeto (`POST /api/projects`)

#### Caso de Teste 2.1.1: Criação com Sucesso
-   **Objetivo:** Verificar se um usuário com a role `MANAGER` consegue criar um projeto.
-   **Endpoint:** `/api/projects`
-   **Método:** `POST`
-   **Headers:** `Authorization: Bearer <TOKEN_DO_MANAGER>`, `Content-Type: application/json`
-   **Body (Raw - JSON):**
    ```json
    {
      "name": "Projeto Alpha",
      "description": "Descrição detalhada do Projeto Alpha."
    }
    ```
-   **Resultado Esperado:**
    -   **Status Code:** `201 Created`
    -   **Body:** Deve conter os dados do projeto criado, incluindo `id`, `name`, `description` e `ownerId`.

#### Caso de Teste 2.1.2: Falha de Autorização (COLLABORATOR)
-   **Objetivo:** Verificar se um usuário com a role `COLLABORATOR` é proibido de criar um projeto.
-   **Endpoint:** `/api/projects`
-   **Método:** `POST`
-   **Headers:** `Authorization: Bearer <TOKEN_DO_COLLABORATOR>`, `Content-Type: application/json`
-   **Body (Raw - JSON):**
    ```json
    {
      "name": "Projeto Beta",
      "description": "Tentativa de criação por um colaborador."
    }
    ```
-   **Resultado Esperado:**
    -   **Status Code:** `403 Forbidden`
    -   **Body:** Vazio ou uma mensagem de erro padrão de acesso negado.

---

### Seção 2.2: Adicionar Membros ao Projeto (`POST /api/projects/{projectId}/members`)

#### Caso de Teste 2.2.1: Sucesso - Dono do Projeto Adiciona Membro
-   **Objetivo:** Verificar se o dono do projeto (MANAGER) consegue adicionar um novo membro.
-   **Endpoint:** `/api/projects/{projectId}/members`
-   **Método:** `POST`
-   **Headers:** `Authorization: Bearer <TOKEN_DO_MANAGER_A>`, `Content-Type: application/json`
-   **Body (Raw - JSON):**
    ```json
    {
      "userId": "<ID_DO_COLABORADOR>"
    }
    ```
-   **Resultado Esperado:**
    -   **Status Code:** `200 OK`
    -   **Verificação Adicional:** Uma nova linha na tabela `project_members` associando o `<projectId>` ao `<ID_DO_COLABORADOR>`.

#### Caso de Teste 2.2.2: Falha (Role) - Colaborador Tenta Adicionar Membro
-   **Objetivo:** Verificar se um usuário sem a role `MANAGER` é proibido de adicionar membros.
-   **Endpoint:** `/api/projects/{projectId}/members`
-   **Método:** `POST`
-   **Headers:** `Authorization: Bearer <TOKEN_DO_COLABORADOR>`, `Content-Type: application/json`
-   **Body (Raw - JSON):**
    ```json
    {
      "userId": "<ID_DE_QUALQUER_USUARIO>"
    }
    ```
-   **Resultado Esperado:**
    -   **Status Code:** `403 Forbidden`

#### Caso de Teste 2.2.3: Falha (Propriedade) - Manager Não-Dono Tenta Adicionar Membro
-   **Objetivo:** Verificar se um `MANAGER` que não é o dono do projeto é proibido de adicionar membros.
-   **Endpoint:** `/api/projects/{projectId}/members`
-   **Método:** `POST`
-   **Headers:** `Authorization: Bearer <TOKEN_DO_MANAGER_B>`, `Content-Type: application/json`
-   **Body (Raw - JSON):**
    ```json
    {
      "userId": "<ID_DE_QUALQUER_USUARIO>"
    }
    ```
-   **Resultado Esperado:**
    -   **Status Code:** `403 Forbidden`

---

### Seção 2.3: Visualização de Meus Projetos (`GET /api/projects`)

#### Caso de Teste 2.3.1: Visualização com Sucesso (Membro de Projetos)
-   **Objetivo:** Verificar se um usuário autenticado consegue listar todos os projetos dos quais é membro.
-   **Endpoint:** `/api/projects`
-   **Método:** `GET`
-   **Headers:** `Authorization: Bearer <TOKEN_DO_USUARIO_MEMBRO>`
-   **Pré-condição:** O usuário deve ser membro de pelo menos um projeto.
-   **Resultado Esperado:**
    -   **Status Code:** `200 OK`
    -   **Body:** Um array JSON contendo a lista de `ProjectResponse` dos projetos aos quais o usuário pertence.
      ```json
      [
        {
          "id": "...",
          "name": "Meu Projeto 1",
          "description": "...",
          "ownerId": "..."
        },
        {
          "id": "...",
          "name": "Meu Projeto 2",
          "description": "...",
          "ownerId": "..."
        }
      ]
      ```

#### Caso de Teste 2.3.2: Visualização com Lista Vazia (Não Membro de Projetos)
-   **Objetivo:** Verificar se o sistema retorna uma lista vazia quando o usuário não é membro de nenhum projeto.
-   **Endpoint:** `/api/projects`
-   **Método:** `GET`
-   **Headers:** `Authorization: Bearer <TOKEN_DO_USUARIO_NAO_MEMBRO>`
-   **Pré-condição:** O usuário não deve ser membro de nenhum projeto.
-   **Resultado Esperado:**
    -   **Status Code:** `200 OK`
    -   **Body:** Um array JSON vazio: `[]`.
