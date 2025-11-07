# Casos de Teste: CL-1 - Adicionar Comentários à Tarefa

Este documento detalha os casos de teste para a história "CL-1: Adicionar Comentários à Tarefa", cobrindo testes de unidade para a lógica de serviço e um guia para validação manual da API.

## 1. Testes de Unidade (JUnit/Mockito)

O objetivo destes testes é garantir que a classe `CommentService` se comporta como esperado, especificamente no método `createComment`, validando a lógica de permissão.

**Dependências (pom.xml):**
```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-api</artifactId>
    <version>5.10.2</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>5.11.0</version>
    <scope>test</scope>
</dependency>
```

**Código do Teste (`/src/test/java/com/taskflow/taskflow/comment/service/CommentServiceTest.java`):**

```java
package com.taskflow.taskflow.comment.service;

import com.taskflow.taskflow.comment.dto.CommentRequest;
import com.taskflow.taskflow.comment.dto.CommentResponse;
import com.taskflow.taskflow.comment.model.Comment;
import com.taskflow.taskflow.comment.repository.CommentRepository;
import com.taskflow.taskflow.project.model.Project;
import com.taskflow.taskflow.project.repository.ProjectMemberRepository;
import com.taskflow.taskflow.security.UserPrincipal;
import com.taskflow.taskflow.task.model.Task;
import com.taskflow.taskflow.task.repository.TaskRepository;
import com.taskflow.taskflow.user.model.UserRole;
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
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @InjectMocks
    private CommentService commentService;

    private UUID taskId;
    private UUID projectId;
    private UUID userId;
    private UserPrincipal currentUser;
    private CommentRequest commentRequest;

    @BeforeEach
    void setUp() {
        taskId = UUID.randomUUID();
        projectId = UUID.randomUUID();
        userId = UUID.randomUUID();

        Usuario user = new Usuario(userId, "Test User", "test@example.com", "password", UserRole.COLLABORATOR, Instant.now());
        currentUser = new UserPrincipal(user);
        commentRequest = new CommentRequest("This is a test comment.");
    }

    @Test
    @DisplayName("Deve criar um comentário com sucesso quando o usuário é membro do projeto")
    void createComment_ShouldSucceed_WhenUserIsProjectMember() {
        // Arrange
        Project project = new Project();
        project.setId(projectId);

        Task task = new Task();
        task.setId(taskId);
        task.setProject(project);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(projectMemberRepository.existsByProjectIdAndUserId(projectId, userId)).thenReturn(true);
        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(currentUser.getUsuario()));

        Comment savedComment = new Comment();
        savedComment.setId(UUID.randomUUID());
        savedComment.setContent(commentRequest.content());
        savedComment.setTask(task);
        savedComment.setAuthor(currentUser.getUsuario());
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        // Act
        CommentResponse response = commentService.createComment(taskId, commentRequest, currentUser);

        // Assert
        assertNotNull(response);
        assertEquals(commentRequest.content(), response.content());
        assertEquals(taskId, response.taskId());
        assertEquals(userId, response.authorId());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException quando o usuário não é membro do projeto")
    void createComment_ShouldThrowAccessDeniedException_WhenUserIsNotProjectMember() {
        // Arrange
        Project project = new Project();
        project.setId(projectId);

        Task task = new Task();
        task.setId(taskId);
        task.setProject(project);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(projectMemberRepository.existsByProjectIdAndUserId(projectId, userId)).thenReturn(false);

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> {
            commentService.createComment(taskId, commentRequest, currentUser);
        });

        verify(commentRepository, never()).save(any(Comment.class));
    }
}
```

## 2. Validação Manual (Postman)

Esta seção descreve os passos para testar o endpoint `POST /api/tasks/{taskId}/comments`.

### Pré-requisitos:
1. A aplicação precisa estar em execução.
2. Tenha um projeto criado com pelo menos uma tarefa. Anote o `taskId`.
3. Tenha dois usuários:
   - **Usuário A:** Membro do projeto.
   - **Usuário B:** NÃO é membro do projeto.
4. Obtenha tokens JWT para ambos os usuários.

---

### Cenário 1: Criação de Comentário com Sucesso (Membro do Projeto)

**Objetivo:** Verificar se um membro do projeto consegue adicionar um comentário a uma tarefa.

| Passo | Ação | Detalhes | Resultado Esperado |
| :--- | :--- | :--- | :--- |
| 1 | **Preparar a Requisição** | **Método:** `POST`<br>**URL:** `http://localhost:8080/api/tasks/<taskId>/comments`<br>**Header:** `Authorization: Bearer <TOKEN_DO_USUARIO_A>`<br>**Body (raw, JSON):**<br>```json<br>{<br>  "content": "Podemos discutir a implementação amanhã?"<br>}<br>```<br>*Substitua `<taskId>` e `<TOKEN_DO_USUARIO_A>`.* | - |
| 2 | **Enviar a Requisição** | Clique em "Send" no Postman. | **Status Code:** `201 Created`<br>**Corpo da Resposta (JSON):**<br>```json<br>{<br>  "id": "...",<br>  "content": "Podemos discutir a implementação amanhã?",<br>  "taskId": "<taskId>",<br>  "authorId": "<ID_DO_USUARIO_A>",<br>  "createdAt": "..."<br>}<br>``` |
| 3 | **Verificar no Banco de Dados (Opcional)** | Conecte-se ao banco de dados e verifique a tabela `comments`. | Um novo registro existe com o conteúdo correto, associado ao `task_id` e `author_id` corretos. |

---

### Cenário 2: Falha de Autorização (NÃO Membro do Projeto)

**Objetivo:** Verificar se um usuário que não é membro do projeto é proibido de comentar.

| Passo | Ação | Detalhes | Resultado Esperado |
| :--- | :--- | :--- | :--- |
| 1 | **Preparar a Requisição** | **Método:** `POST`<br>**URL:** `http://localhost:8080/api/tasks/<taskId>/comments`<br>**Header:** `Authorization: Bearer <TOKEN_DO_USUARIO_B>`<br>**Body (raw, JSON):**<br>```json<br>{<br>  "content": "Tentativa de comentário indevido."<br>}<br>```<br>*Substitua `<taskId>` e `<TOKEN_DO_USUARIO_B>`.* | - |
| 2 | **Enviar a Requisição** | Clique em "Send" no Postman. | **Status Code:** `403 Forbidden`<br>**Corpo da Resposta:** Vazio ou uma mensagem de erro de acesso negado. |
| 3 | **Verificar no Banco de Dados (Opcional)** | Verifique a tabela `comments`. | Nenhum novo comentário deve ter sido criado. |
