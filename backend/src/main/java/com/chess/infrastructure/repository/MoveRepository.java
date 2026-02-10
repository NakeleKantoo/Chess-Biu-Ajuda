package com.chess.infrastructure.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chess.infrastructure.persistence.entity.MoveEntity;

public interface MoveRepository extends JpaRepository<MoveEntity, UUID> {
    
}
