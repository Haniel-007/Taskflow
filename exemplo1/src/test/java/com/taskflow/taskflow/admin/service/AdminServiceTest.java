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
