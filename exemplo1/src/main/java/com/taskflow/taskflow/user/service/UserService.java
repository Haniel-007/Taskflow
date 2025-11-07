package com.taskflow.taskflow.user.service;

import com.taskflow.taskflow.user.model.Usuario;
import com.taskflow.taskflow.user.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UsuarioRepository usuarioRepository;

    public Usuario validateAndGetUserByUsername(String username) {
        return usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }

    public List<Usuario> getAllUsers() {
        return usuarioRepository.findAll();
    }

    public Usuario getUserByName(String name) {
        return usuarioRepository.findByName(name);
    }
}
