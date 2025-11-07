package com.taskflow.taskflow.admin.service;

import com.taskflow.taskflow.core.exception.ResourceNotFoundException;
import com.taskflow.taskflow.user.model.Usuario;
import com.taskflow.taskflow.user.model.UserRole;
import com.taskflow.taskflow.user.model.UserStatus;
import com.taskflow.taskflow.user.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UsuarioRepository usuarioRepository;

    @Transactional
    public void updateUserStatus(UUID userId, String status) {
        Usuario user = usuarioRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setStatus(UserStatus.valueOf(status));
        usuarioRepository.save(user);
    }

    @Transactional
    public void updateUserRole(UUID userId, String role) {
        Usuario user = usuarioRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setRole(UserRole.valueOf(role));
        usuarioRepository.save(user);
    }
}
