package com.taskflow.taskflow.config;

import com.taskflow.taskflow.user.model.UserRole;
import com.taskflow.taskflow.user.model.Usuario;
import com.taskflow.taskflow.user.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // --- Configurações do Admin Inicial ---
    private static final String ADMIN_EMAIL = "admin@taskflow.com";
    private static final String ADMIN_NOME = "Administrador";
    private static final String ADMIN_PASSWORD = "PasswordSegura123";

    @Override
    public void run(String... args) throws Exception {
        Optional<Usuario> adminExists = usuarioRepository.findFirstByRole(UserRole.ADMIN);

        if (adminExists.isEmpty()) {
            Usuario admin = new Usuario();
            admin.setEmail(ADMIN_EMAIL);
            admin.setName(ADMIN_NOME);
            
            String hashedPassword = passwordEncoder.encode(ADMIN_PASSWORD);
            admin.setPassword(hashedPassword);
            admin.setRole(UserRole.ADMIN);
            
            usuarioRepository.save(admin);

            // System.out.println("--- ADMIN INICIAL CRIADO ---");
            // System.out.println("Email: " + ADMIN_EMAIL);
            // System.out.println("Senha: " + ADMIN_PASSWORD);
            // System.out.println("----------------------------");
        }
    }
}