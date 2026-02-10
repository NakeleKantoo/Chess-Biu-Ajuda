package com.chess.infrastructure.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chess.infrastructure.persistence.entity.GameEntity;

public interface GameRepository extends JpaRepository<GameEntity, UUID> {
    
}
