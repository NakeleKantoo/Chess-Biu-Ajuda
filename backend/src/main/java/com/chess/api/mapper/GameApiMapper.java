package com.chess.api.mapper;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import com.chess.api.dto.request.game.GameConfigDTO;
import com.chess.api.dto.response.game.GameDTO;
import com.chess.api.dto.response.game.base.BoardDTO;
import com.chess.api.dto.response.game.base.ClockDTO;
import com.chess.api.dto.response.game.base.MoveDTO;
import com.chess.app.service.move.MoveValidator;
import com.chess.domain.model.base.Color;
import com.chess.domain.model.board.Board;
import com.chess.domain.model.game.Game;
import com.chess.domain.model.game.GameConfig;
import com.chess.domain.model.game.GameEndReason;
import com.chess.domain.model.game.GameState;
import com.chess.domain.model.game.GameConfig.GameType;
import com.chess.domain.model.game.GameConfig.TimeControl;
import com.chess.domain.model.move.Move;

public class GameApiMapper {
    
    public static GameConfig fromGameConfigDTO(GameConfigDTO configDTO) {
        GameType gameType = configDTO.gameType();
        TimeControl timeControl = configDTO.timeControl();
        Color startingColor = configDTO.startingColor() == null ? Color.WHITE : configDTO.startingColor();
        Color creatorColor = Color.fromPlayerColorPreference(configDTO.playerColorPreference());

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
                startingColor,
                creatorColor
            );
        } else {
            // Para controles de tempo pré-definidos, os tempos serão definidos pelo ChessClockBuilder, então passamos null aqui.
            config = GameConfig.of(timeControl, gameType, startingColor, creatorColor);
        }

        return config;
    }

    public static GameDTO toDTO(Game game, UUID gameId) {
        UUID id = gameId;
        BoardDTO boardDTO = getBoardDTO(game.getCurrentBoard());
        GameState state = game.getGameState();
        GameEndReason endReason = game.getGameEndReason();
        ClockDTO clockDTO = getClockDTO(game);
        List<MoveDTO> moveHistory = getMoveHistory(game.getMoveHistory());
        Map<String, List<String>> legalMoves = getLegalMoves(game.getCurrentBoard());

        return new GameDTO(id, boardDTO, state, endReason, clockDTO, moveHistory, legalMoves);
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
            .map(GameApiMapper::getMoveDTO)
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
