# Casos de Teste para CA-1: Painel de Gerenciamento de Usuários para Admin

## Cenário: Visualizar todos os usuários como ADMIN

**Critério de Aceitação:** DADO que sou um ADMIN e estou autenticado, QUANDO eu envio uma requisição GET para /api/admin/users, ENTÃO o sistema deve retornar 200 OK com a lista de todos os usuários.

**Casos de Teste:**

*   **CT-CA1-001:** Verificar que um ADMIN autenticado pode visualizar a lista de todos os usuários.
    *   **Pré-condições:** Usuário ADMIN autenticado.
    *   **Passos:**
        1.  Realizar uma requisição GET para `/api/admin/users`.
    *   **Resultado Esperado:**
        *   Status da resposta: 200 OK.
        *   Corpo da resposta: Uma lista JSON contendo todos os usuários registrados no sistema.

## Cenário: Promover usuário a MANAGER como ADMIN

**Critério de Aceitação:** DADO que sou um ADMIN, QUANDO eu envio uma requisição PATCH para /api/admin/users/{userId}/role com o role 'MANAGER', ENTÃO o usuário deve ser promovido e o sistema retornar 200 OK.

**Casos de Teste:**

*   **CT-CA1-002:** Verificar que um ADMIN pode promover um usuário existente para o role 'MANAGER'.
    *   **Pré-condições:** Usuário ADMIN autenticado, ID de um usuário existente (não-ADMIN) conhecido.
    *   **Passos:**
        1.  Realizar uma requisição PATCH para `/api/admin/users/{userId}/role` (substituir `{userId}` pelo ID do usuário) com o corpo da requisição `{"role": "MANAGER"}`.
    *   **Resultado Esperado:**
        *   Status da resposta: 200 OK.
        *   Verificar no banco de dados ou através de uma requisição GET subsequente que o role do usuário foi atualizado para 'MANAGER'.

## Cenário: Acesso negado para usuários não-ADMIN

**Critério de Aceitação:** DADO que um usuário não-ADMIN tenta acessar qualquer endpoint /api/admin/**, ENTÃO o sistema deve retornar o status 403 Forbidden.

**Casos de Teste:**

*   **CT-CA1-003:** Verificar que um usuário comum (não-ADMIN) não pode acessar o endpoint de listagem de usuários.
    *   **Pré-condições:** Usuário comum (não-ADMIN) autenticado.
    *   **Passos:**
        1.  Realizar uma requisição GET para `/api/admin/users`.
    *   **Resultado Esperado:**
        *   Status da resposta: 403 Forbidden.

        *   Status da resposta: 403 Forbidden.



## Testes de Unidade (JUnit/Mockito)



O objetivo do teste de unidade é verificar se a lógica do `AdminService` está correta, garantindo que ele gerencia os usuários (status, role) de forma adequada. O `UsuarioRepository` será mockado para isolar o serviço.



### Código do Teste: `AdminServiceTest.java`



```java

package com.taskflow.taskflow.admin.service;



import com.taskflow.taskflow.core.exception.ResourceNotFoundException;

import com.taskflow.taskflow.user.model.Usuario;

import com.taskflow.taskflow.user.model.UserRole;

import com.taskflow.taskflow.user.model.UserStatus;

import com.taskflow.taskflow.user.repository.UsuarioRepository;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;

import org.mockito.Mock;

import org.mockito.MockitoAnnotations;



import java.util.Collections;

import java.util.List;

import java.util.Optional;

import java.util.UUID;



import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;



public class AdminServiceTest {



    @Mock

    private UsuarioRepository usuarioRepository;



    @InjectMocks

    private AdminService adminService;



    @BeforeEach

    void setUp() {

        MockitoAnnotations.openMocks(this);

    }



    @Test

    void testGetAllUsers() {

        // Arrange

        Usuario user = new Usuario();

        when(usuarioRepository.findAll()).thenReturn(Collections.singletonList(user));



        // Act

        List<Usuario> users = adminService.getAllUsers();



        // Assert

        assertEquals(1, users.size());

        verify(usuarioRepository, times(1)).findAll();

    }



    @Test

    void testUpdateUserStatus() {

        // Arrange

        UUID userId = UUID.randomUUID();

        Usuario user = new Usuario();

        user.setId(userId);

        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(user));



        // Act

        adminService.updateUserStatus(userId, "INACTIVE");



        // Assert

        assertEquals(UserStatus.INACTIVE, user.getStatus());

        verify(usuarioRepository, times(1)).save(user);

    }



    @Test

    void testUpdateUserStatus_UserNotFound() {

        // Arrange

        UUID userId = UUID.randomUUID();

        when(usuarioRepository.findById(userId)).thenReturn(Optional.empty());



        // Act & Assert

        assertThrows(ResourceNotFoundException.class, () -> {

            adminService.updateUserStatus(userId, "INACTIVE");

        });

    }



    @Test

    void testUpdateUserRole() {

        // Arrange

        UUID userId = UUID.randomUUID();

        Usuario user = new Usuario();

        user.setId(userId);

        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(user));



        // Act

        adminService.updateUserRole(userId, "MANAGER");



        // Assert

        assertEquals(UserRole.MANAGER, user.getRole());

        verify(usuarioRepository, times(1)).save(user);

    }



    @Test

    void testUpdateUserRole_UserNotFound() {

        // Arrange

        UUID userId = UUID.randomUUID();

        when(usuarioRepository.findById(userId)).thenReturn(Optional.empty());



        // Act & Assert

        assertThrows(ResourceNotFoundException.class, () -> {

            adminService.updateUserRole(userId, "MANAGER");

        });

    }

}


