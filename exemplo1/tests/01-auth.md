# Casos de Teste - ST-01 & ST-02: Autenticação

Este documento detalha os casos de teste para as funcionalidades de **Cadastro** e **Login** de usuário.

## 1. Testes de Unidade (JUnit 5 & Mockito)

### Código do Teste (`AuthServiceTest.java`)

```java
package com.taskflow.taskflow.user.service;

import com.taskflow.taskflow.security.JwtService;
import com.taskflow.taskflow.user.dto.CadastroRequest;
import com.taskflow.taskflow.user.dto.LoginRequest;
import com.taskflow.taskflow.user.dto.LoginResponse;
import com.taskflow.taskflow.user.model.UserRole;
import com.taskflow.taskflow.user.model.Usuario;
import com.taskflow.taskflow.user.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtService jwtService;

    @InjectMocks private AuthService authService;

    @Captor private ArgumentCaptor<Usuario> usuarioArgumentCaptor;

    private CadastroRequest cadastroRequest;
    private LoginRequest loginRequest;
    private Usuario user;

    @BeforeEach
    void setUp() {
        cadastroRequest = new CadastroRequest("Test User", "test@example.com", "password123");
        loginRequest = new LoginRequest("test@example.com", "password123");
        user = new Usuario(UUID.randomUUID(), "Test User", "test@example.com", "hashedPassword", UserRole.COLLABORATOR, null);
    }

    @Test
    @DisplayName("Deve registrar um novo usuário com sucesso")
    void registrar_Success() {
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");

        authService.registrar(cadastroRequest);

        verify(usuarioRepository).save(usuarioArgumentCaptor.capture());
        Usuario savedUser = usuarioArgumentCaptor.getValue();

        assertThat(savedUser.getName()).isEqualTo("Test User");
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
        assertThat(savedUser.getPassword()).isEqualTo("hashedPassword");
        assertThat(savedUser.getRole()).isEqualTo(UserRole.COLLABORATOR);
    }

    @Test
    @DisplayName("Deve lançar exceção 409 Conflict se o e-mail já existir")
    void registrar_EmailAlreadyExists_ThrowsConflictException() {
        when(usuarioRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new Usuario()));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> authService.registrar(cadastroRequest));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve autenticar usuário e retornar JWT com sucesso")
    void login_Success() {
        when(usuarioRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("fake.jwt.token");

        LoginResponse response = authService.login(loginRequest);

        verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken("test@example.com", "password123"));
        assertThat(response.jwtToken()).isEqualTo("fake.jwt.token");
    }

    @Test
    @DisplayName("Deve lançar exceção de autenticação para credenciais inválidas")
    void login_InvalidCredentials_ThrowsException() {
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));
    }
}
```

## 2. Validação Manual (API Endpoint - Postman)

**URL Base:** `http://localhost:8080`

---

### Seção 2.1: Cadastro (`POST /api/auth/register`)

#### Caso de Teste 2.1.1: Cadastro com Sucesso
-   **Resultado Esperado:** Status `201 Created`.

#### Caso de Teste 2.1.2: E-mail Duplicado
-   **Resultado Esperado:** Status `409 Conflict`.

#### Caso de Teste 2.1.3: Falha de Validação
-   **Resultado Esperado:** Status `400 Bad Request`.

---

### Seção 2.2: Login (`POST /api/auth/login`)

#### Caso de Teste 2.2.1: Login com Sucesso
-   **Objetivo:** Verificar se um usuário registrado pode se autenticar e receber um token.
-   **Pré-condição:** Um usuário deve ter sido criado previamente (ex: `sofia.andrade@example.com` com senha `strongpassword123`).
-   **Método:** `POST`
-   **Endpoint:** `/api/auth/login`
-   **Headers:** `Content-Type: application/json`
-   **Body (Raw - JSON):**
    ```json
    {
      "email": "sofia.andrade@example.com",
      "password": "strongpassword123"
    }
    ```
-   **Resultado Esperado:**
    -   **Status Code:** `200 OK`
    -   **Body:** Deve conter o token JWT.
      ```json
      {
        "jwtToken": "ey..."
      }
      ```

#### Caso de Teste 2.2.2: Credenciais Inválidas (Senha Incorreta)
-   **Objetivo:** Verificar se o sistema nega o acesso com uma senha incorreta.
-   **Método:** `POST`
-   **Endpoint:** `/api/auth/login`
-   **Headers:** `Content-Type: application/json`
-   **Body (Raw - JSON):**
    ```json
    {
      "email": "sofia.andrade@example.com",
      "password": "wrongpassword"
    }
    ```
-   **Resultado Esperado:**
    -   **Status Code:** `401 Unauthorized`

#### Caso de Teste 2.2.3: Usuário Inexistente
-   **Objetivo:** Verificar se o sistema nega o acesso para um e-mail não cadastrado.
-   **Método:** `POST`
-   **Endpoint:** `/api/auth/login`
-   **Headers:** `Content-Type: application/json`
-   **Body (Raw - JSON):**
    ```json
    {
      "email": "nouser@example.com",
      "password": "anypassword"
    }
    ```
-   **Resultado Esperado:**
    -   **Status Code:** `401 Unauthorized`


