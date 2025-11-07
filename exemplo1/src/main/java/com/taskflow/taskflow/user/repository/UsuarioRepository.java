package com.taskflow.taskflow.user.repository;

import com.taskflow.taskflow.user.model.UserRole;
import com.taskflow.taskflow.user.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findFirstByRole(UserRole admin);

    Usuario findByName(String name);
}
