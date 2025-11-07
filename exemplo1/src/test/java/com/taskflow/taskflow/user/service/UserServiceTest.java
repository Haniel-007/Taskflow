package com.taskflow.taskflow.user.service;

import com.taskflow.taskflow.user.model.Usuario;
import com.taskflow.taskflow.user.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UserService userService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setEmail("test@example.com");
        usuario.setName("Test User");
    }

    @Test
    @DisplayName("Deve retornar o usuário quando o email existir")
    void validateAndGetUserByUsername_ShouldReturnUser_WhenEmailExists() {
        when(usuarioRepository.findByEmail("test@example.com")).thenReturn(Optional.of(usuario));

        Usuario result = userService.validateAndGetUserByUsername("test@example.com");

        assertNotNull(result);
        assertEquals("Test User", result.getName());
        verify(usuarioRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException quando o email não existir")
    void validateAndGetUserByUsername_ShouldThrowException_WhenEmailDoesNotExist() {
        when(usuarioRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.validateAndGetUserByUsername("nonexistent@example.com");
        });

        verify(usuarioRepository, times(1)).findByEmail("nonexistent@example.com");
    }

    @Test
    @DisplayName("Deve retornar todos os usuários")
    void getAllUsers_ShouldReturnAllUsers() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        List<Usuario> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test User", result.get(0).getName());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia quando não houver usuários")
    void getAllUsers_ShouldReturnEmptyList_WhenNoUsersExist() {
        when(usuarioRepository.findAll()).thenReturn(Collections.emptyList());

        List<Usuario> result = userService.getAllUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar o usuário quando o nome existir")
    void getUserByName_ShouldReturnUser_WhenNameExists() {
        when(usuarioRepository.findByName("Test User")).thenReturn(usuario);

        Usuario result = userService.getUserByName("Test User");

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        verify(usuarioRepository, times(1)).findByName("Test User");
    }

    @Test
    @DisplayName("Deve retornar nulo quando o nome não existir")
    void getUserByName_ShouldReturnNull_WhenNameDoesNotExist() {
        when(usuarioRepository.findByName("Nonexistent User")).thenReturn(null);

        Usuario result = userService.getUserByName("Nonexistent User");

        assertNull(result);
        verify(usuarioRepository, times(1)).findByName("Nonexistent User");
    }
}
