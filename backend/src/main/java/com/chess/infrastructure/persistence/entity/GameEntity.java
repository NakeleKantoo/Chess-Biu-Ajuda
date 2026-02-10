package com.chess.infrastructure.persistence.entity;

import java.util.List;
import java.util.UUID;

import com.chess.domain.model.game.GameConfig.GameType;
import com.chess.domain.model.game.GameEndReason;
import com.chess.domain.model.game.GameState;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "games")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameEntity {

    @Id
    private UUID id;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "sequence_number")
    private List<MoveEntity> moves;

    @ManyToOne 
    @JoinColumn(name = "white_player_id", nullable = false)
    private UserEntity whitePlayer;

    @ManyToOne
    @JoinColumn(name = "black_player_id", nullable = false)
    private UserEntity blackPlayer;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GameState gameState;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GameEndReason gameEndReason;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GameType gameType;

    @Column(nullable = false)
    private Long whiteTimeRemaining;

    @Column(nullable = false)
    private Long blackTimeRemaining;
    
    @Column(nullable = false)
    private String starterFen;

}
