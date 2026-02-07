package com.chess.mapper;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import com.chess.dto.request.GameConfigDTO;
import com.chess.dto.response.BoardDTO;
import com.chess.dto.response.ClockDTO;
import com.chess.dto.response.GameResponseDTO;
import com.chess.dto.response.MoveDTO;
import com.chess.entity.base.Color;
import com.chess.entity.board.Board;
import com.chess.entity.game.Game;
import com.chess.entity.game.GameConfig;
import com.chess.entity.game.GameEndReason;
import com.chess.entity.game.GameState;
import com.chess.entity.game.GameConfig.GameType;
import com.chess.entity.game.GameConfig.TimeControl;
import com.chess.entity.move.Move;
import com.chess.service.move.MoveValidator;

public class GameMapper {
    
    public static GameConfig fromGameConfigDTO(GameConfigDTO configDTO) {
        GameType gameType = GameType.fromString(configDTO.gameType());
        TimeControl timeControl = TimeControl.fromString(configDTO.timeControl());
        Color startingColor = Color.fromPlayerColorPreference(configDTO.playerColorPreference());

        GameConfig config;

        if (timeControl == TimeControl.CUSTOM) {
            Objects.requireNonNull(configDTO.whiteTimeRemaining(), "O tempo para as brancas é obrigatório para controle de tempo CUSTOM.");
            Objects.requireNonNull(configDTO.blackTimeRemaining(), "O tempo para as pretas é obrigatório para controle de tempo CUSTOM.");
            Objects.requireNonNull(configDTO.incrementMillis(), "O incremento de tempo é obrigatório para controle de tempo CUSTOM.");
            
            config = GameConfig.custom(
                gameType,
                configDTO.whiteTimeRemaining(),
                configDTO.blackTimeRemaining(),
                configDTO.incrementMillis(),
                startingColor
            );
        } else {
            // Para controles de tempo pré-definidos, os tempos serão definidos pelo ChessClockBuilder, então passamos null aqui.
            config = GameConfig.of(timeControl, gameType, startingColor);
        }

        return config;
    }

    public static GameResponseDTO toDTO(Game game) {
        UUID id = UUID.randomUUID(); // TODO: Substituir por ID real do jogo quando implementado
        BoardDTO boardDTO = getBoardDTO(game.getCurrentBoard());
        GameState state = game.getGameState();
        GameEndReason endReason = game.getGameEndReason();
        ClockDTO clockDTO = getClockDTO(game);
        List<MoveDTO> moveHistory = getMoveHistory(game.getMoveHistory());
        Map<String, List<String>> legalMoves = getLegalMoves(game.getCurrentBoard());

        return new GameResponseDTO(id, boardDTO, state, endReason, clockDTO, moveHistory, legalMoves);
    }

    private static BoardDTO getBoardDTO(Board board) {
        return new BoardDTO(board.toFen());
    }

    private static ClockDTO getClockDTO(Game game) {
        return new ClockDTO(
            game.getTimeRemaining(Color.WHITE),
            game.getTimeRemaining(Color.BLACK),
            game.getLastMoveTimestamp(),
            game.isClockRunning()
        );
    }

    private static MoveDTO getMoveDTO(Move move) {
        String san = move.getSan();
        String uci = move.getUci();
        return new MoveDTO(san, uci);
    }

    private static List<MoveDTO> getMoveHistory(List<Move> moveHistory) {
        return moveHistory.stream()
            .map(GameMapper::getMoveDTO)
            .toList();
    }

    private static Map<String, List<String>> getLegalMoves(Board board) {
        MoveValidator validator = MoveValidator.of(board);
        List<Move> legalMoves = validator.getLegalMoves();

        return legalMoves.stream()
            .collect(Collectors.groupingBy(
                move -> move.getFrom().toString(),
                Collectors.mapping(move -> move.getTo().toString(), Collectors.toList())
            ));
    }

}
