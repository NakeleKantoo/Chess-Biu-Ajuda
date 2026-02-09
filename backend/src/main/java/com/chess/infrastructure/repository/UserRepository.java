package com.chess.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chess.domain.model.user.User;

public interface UserRepository extends JpaRepository<User, UUID> {
    // Necessário para o Login
    Optional<User> findByUsername(String username);

    // Necessário para o Cadastro
    Boolean existsByUsername(String username);
}
