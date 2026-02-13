package com.chess.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.chess.api.dto.response.ErrorResponse;
import com.chess.domain.exception.game.DuplicateGameException;
import com.chess.domain.exception.game.GameNotFoundException;
import com.chess.domain.exception.game.GameSessionNotFoundException;
import com.chess.domain.exception.game.InvalidGameAction;
import com.chess.domain.exception.game.InvalidGameStateException;
import com.chess.domain.exception.game.PendingGameNotFoundException;
import com.chess.domain.exception.move.InvalidMoveException;
import com.chess.domain.exception.move.InvalidPositionNotationException;
import com.chess.domain.exception.move.InvalidPromotionPieceException;
import com.chess.domain.exception.player.NotPlayerTurnException;
import com.chess.domain.exception.player.PlayerNotInGameException;
import com.chess.domain.exception.player.SelfJoinGameException;
import com.chess.domain.exception.user.DuplicateUsernameException;
import com.chess.domain.exception.user.UserNotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import tools.jackson.databind.exc.InvalidFormatException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalApiExceptionHandler {

    // ---------- Tratamento de Exceções Genéricas ----------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
        Exception ex,
        HttpServletRequest request
    ) {
        return buildResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Internal Server Error",
            new RuntimeException("Ocorreu um erro inesperado. Tente novamente mais tarde."),
            request
        );
    }

    // ---------- Tratamento de Exceções do Java ----------
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(
        IllegalStateException ex,
        HttpServletRequest request
    ) {
        return buildResponse(
            HttpStatus.BAD_REQUEST,
            "Invalid Action",
            ex,
            request);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
        IllegalArgumentException ex, 
        HttpServletRequest request
    ) {
        return buildResponse(
            HttpStatus.BAD_REQUEST,
            "Invalid Argument",
            ex, request);
    }

    // ---------- Tratamento de Exceções do Spring ----------
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJson(
        HttpMessageNotReadableException ex,
        HttpServletRequest request
    ) {
        String message = "Corpo da requisição contém valores inválidos";
        
        // Tenta extrair detalhes se for erro de formato
        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException ife) {
            String fieldName = ife.getPath().get(0).getPropertyName();
            message = String.format("Valor inválido para o campo '%s'", fieldName);
        }
        
        return buildResponse(
            HttpStatus.BAD_REQUEST,
            "Invalid Request Body",
            new RuntimeException(message),
            request
        );
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
        MethodArgumentNotValidException ex, 
        HttpServletRequest request
    ) {
        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(
                FieldError::getField,
                error -> error.getDefaultMessage() != null 
                    ? error.getDefaultMessage() 
                    : "Valor inválido"
            ));
        
        return buildResponse(
            HttpStatus.BAD_REQUEST,
            "Validation Failed",
            ex, request,
            Map.of("fieldErrors", fieldErrors));
    }

    // ---------- Tratamento de Exceções de Domínio ----------
    // NOT FOUND - 404
    @ExceptionHandler(GameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleGameNotFound(
        GameNotFoundException ex,
        HttpServletRequest request
    ) {
        return buildResponse(
            HttpStatus.NOT_FOUND,
            "Game Not Found",
            ex, request,
            Map.of("gameId", ex.getGameId()));
    }
    @ExceptionHandler(PendingGameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePendingGameNotFound(
        PendingGameNotFoundException ex,
        HttpServletRequest request
    ) {
        return buildResponse(
            HttpStatus.NOT_FOUND,
            "Pending Game Not Found",
            ex, request,
            Map.of("gameCode", ex.getGameCode()));
    }
    @ExceptionHandler(GameSessionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleGameSessionNotFound(
        GameSessionNotFoundException ex,
        HttpServletRequest request
    ) {
        return buildResponse(
            HttpStatus.NOT_FOUND,
            "Game Session Not Found",
            ex, request,
            Map.of("gameId", ex.getGameId()));
    }
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(
        UserNotFoundException ex,
        HttpServletRequest request
    ) {
        return buildResponse(
            HttpStatus.NOT_FOUND,
            "User Not Found",
            ex, request,
            Map.of("userId", ex.getUserId()));
    }

    // CONFLICT - 409
    @ExceptionHandler(DuplicateGameException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateGame(
        DuplicateGameException ex,
        HttpServletRequest request
    ) {
        return buildResponse(
            HttpStatus.CONFLICT,
            "Duplicate Game",
            ex, request,
            Map.of("gameId", ex.getGameId()));
    }
    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateUsername(
        DuplicateUsernameException ex,
        HttpServletRequest request
    ) {
        return buildResponse(
            HttpStatus.CONFLICT,
            "Duplicate Username",
            ex, request,
            Map.of("username", ex.getUsername()));
    }

    // BAD REQUEST - 400 (Regras de Negócio)
    @ExceptionHandler(InvalidMoveException.class)
    public ResponseEntity<ErrorResponse> handleInvalidMove(
        InvalidMoveException ex,
        HttpServletRequest request
    ) {
        String type = ex.getTo() == null 
            ? "Invalid Move - No Piece at Origin" 
            : "Invalid Move";
    
        Map<String, Object> details = ex.getTo() == null
            ? Map.of("from", ex.getFrom().toString())
            : Map.of("from", ex.getFrom().toString(), "to", ex.getTo().toString());
        
        return buildResponse(
            HttpStatus.BAD_REQUEST,
            type,
            ex, request,
            details);
    }
    @ExceptionHandler(InvalidGameStateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidGameState(
        InvalidGameStateException ex,
        HttpServletRequest request
    ) {
        return buildResponse(
            HttpStatus.BAD_REQUEST,
            "Invalid Game State",
            ex, request,
            Map.of("gameId", ex.getGameId(), "currentState", ex.getCurrentState()));
    }
    @ExceptionHandler(NotPlayerTurnException.class)
    public ResponseEntity<ErrorResponse> handleNotPlayerTurn(
        NotPlayerTurnException ex,
        HttpServletRequest request
    ) {
        return buildResponse(
            HttpStatus.BAD_REQUEST,
            "Not Player Turn",
            ex, request,
            Map.of("playerId", ex.getPlayerId()));
    }
    @ExceptionHandler(SelfJoinGameException.class)
    public ResponseEntity<ErrorResponse> handleSelfJoinGame(
        SelfJoinGameException ex,
        HttpServletRequest request
    ) {
        return buildResponse(
            HttpStatus.BAD_REQUEST,
            "Self Join Game",
            ex, request,
            Map.of("playerId", ex.getPlayerId()));
    }
    @ExceptionHandler(InvalidGameAction.class)
    public ResponseEntity<ErrorResponse> handleInvalidGameAction(
        InvalidGameAction ex,
        HttpServletRequest request
    ) {
        return buildResponse(
            HttpStatus.BAD_REQUEST,
            "Invalid Game Action",
            ex, request,
            Map.of("action", ex.getAction()));
    }
    
    // BAD REQUEST - 400 (Validação de Entrada)
    @ExceptionHandler(InvalidPromotionPieceException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPromotionPiece(
        InvalidPromotionPieceException ex,
        HttpServletRequest request
    ) {
        return buildResponse(
            HttpStatus.BAD_REQUEST,
            "Invalid Promotion Piece",
            ex, request,
            Map.of("piece", ex.getPiece()));
    }
    @ExceptionHandler(InvalidPositionNotationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPositionNotation(
        InvalidPositionNotationException ex,
        HttpServletRequest request
    ) {
        return buildResponse(
            HttpStatus.BAD_REQUEST,
            "Invalid Position Notation",
            ex, request,
            Map.of("notation", ex.getNotation()));
    }

    // FORBIDDEN - 403
    @ExceptionHandler(PlayerNotInGameException.class)
    public ResponseEntity<ErrorResponse> handlePlayerNotInGame(
        PlayerNotInGameException ex,
        HttpServletRequest request
    ) {
        return buildResponse(
            HttpStatus.FORBIDDEN,
            "Player Not In Game",
            ex, request,
            Map.of("playerId", ex.getPlayerId()));
    }

    // ---------- Métodos Auxiliares ----------
    private ResponseEntity<ErrorResponse> buildResponse(
        HttpStatus status,
        String type,
        Exception ex,
        HttpServletRequest request,
        Map<String, Object> details
    ) {
        ErrorResponse error = new ErrorResponse(
            type,
            ex.getMessage(),
            LocalDateTime.now(),
            request.getRequestURI(),
            details);
        return ResponseEntity.status(status).body(error);
    }

    private ResponseEntity<ErrorResponse> buildResponse(
        HttpStatus status,
        String type,
        Exception ex,
        HttpServletRequest request
    ) {
        return buildResponse(status, type, ex, request, null);
    }

}
