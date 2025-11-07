# Test Plan: DR-1/DR-2 - Dashboard Principal

Este documento detalha os casos de teste para a funcionalidade do Dashboard Principal, que exibe um resumo das tarefas por status.

## 1. Testes de Unidade (JUnit/Mockito)

O objetivo do teste de unidade é verificar se a lógica do `DashboardService` está correta, garantindo que ele conta as tarefas por status de forma precisa. O `TaskRepository` será mockado para isolar o serviço.

### Código do Teste: `DashboardServiceTest.java`

```java
package com.taskflow.taskflow.dashboard.service;

import com.taskflow.taskflow.dashboard.dto.DashboardResponse;
import com.taskflow.taskflow.security.UserPrincipal;
import com.taskflow.taskflow.task.model.TaskStatus;
import com.taskflow.taskflow.task.repository.TaskRepository;
import com.taskflow.taskflow.user.model.Usuario;
import com.taskflow.taskflow.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class DashboardServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetDashboardSummary() {
        // Arrange
        UserPrincipal currentUserPrincipal = new UserPrincipal(new Usuario());
        Usuario currentUser = new Usuario();
        when(userService.validateAndGetUserByUsername(currentUserPrincipal.getUsername())).thenReturn(currentUser);
        when(taskRepository.countByStatusAndAssignee(TaskStatus.TODO, currentUser)).thenReturn(5L);
        when(taskRepository.countByStatusAndAssignee(TaskStatus.IN_PROGRESS, currentUser)).thenReturn(3L);
        when(taskRepository.countByStatusAndAssignee(TaskStatus.DONE, currentUser)).thenReturn(2L);

        // Act
        DashboardResponse summary = dashboardService.getDashboardSummary(currentUserPrincipal);

        // Assert
        assertEquals(5L, summary.getTodoCount());
        assertEquals(3L, summary.getInProgressCount());
        assertEquals(2L, summary.getDoneCount());
    }
}
```

## 2. Validação Manual (Postman)

Estes testes validam o endpoint `/api/dashboard/summary` de ponta a ponta, incluindo a camada de segurança.

**Pré-requisitos:**
- A aplicação deve estar em execução.
- Um usuário com o role `COLLABORATOR` deve existir no banco de dados.
- O usuário deve ter tarefas atribuídas a ele com diferentes status (TODO, IN_PROGRESS, DONE).

---

### Cenário 1: Sucesso (Usuário Autenticado)

**Objetivo:** Verificar se um usuário autenticado recebe as contagens corretas de tarefas.

**Passos:**
1.  Obtenha um token JWT válido para um usuário `COLLABORATOR`.
2.  Configure uma nova requisição no Postman:
    -   **Método:** `GET`
    -   **URL:** `http://localhost:8080/api/dashboard/summary`
    -   **Headers:**
        -   `Authorization`: `Bearer <SEU_TOKEN_JWT>`
3.  Envie a requisição.

**Resultado Esperado:**
-   **Status Code:** `200 OK`
-   **Corpo da Resposta (JSON):** Um objeto contendo as contagens de tarefas do usuário.
    ```json
    {
        "todoCount": 5,
        "inProgressCount": 2,
        "doneCount": 1
    }
    ```
    *(Nota: Os valores exatos dependerão dos dados de teste do usuário).*

---

### Cenário 2: Falha (Não Autenticado)

**Objetivo:** Verificar se o endpoint é protegido e retorna um erro `401 Unauthorized` para usuários não autenticados.

**Passos:**
1.  Configure uma nova requisição no Postman:
    -   **Método:** `GET`
    -   **URL:** `http://localhost:8080/api/dashboard/summary`
2.  Certifique-se de que **nenhum** token de autorização está sendo enviado.
3.  Envie a requisição.

**Resultado Esperado:**
-   **Status Code:** `401 Unauthorized`
