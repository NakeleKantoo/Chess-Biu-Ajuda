package com.chess.api.exception;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.ControllerAdvice;

import com.chess.api.dto.response.ErrorResponse;
import com.chess.domain.exception.ChessException;

@ControllerAdvice
public class GlobalSocketExceptionHandler {

    // ---------- Tratamento de Exceções Genéricas ----------
    @MessageExceptionHandler(Exception.class)
    @SendToUser("/queue/errors")
    public ErrorResponse handleGenericException(Exception ex) {
        return buildResponse(
            "Internal Server Error",
            "Ocorreu um erro inesperado no servidor de jogo.",
            Collections.emptyMap()
        );
    }

    // ---------- Tratamento de Erros de Lógica Comuns ----------
    @MessageExceptionHandler({IllegalStateException.class, IllegalArgumentException.class})
    @SendToUser("/queue/errors")
    public ErrorResponse handleIllegalState(RuntimeException ex) {
        return buildResponse(
            "Invalid Action",
            ex.getMessage(),
            Collections.emptyMap()
        );
    }

    // ---------- Tratamento de Exceções de Domínio ----------
    @MessageExceptionHandler(ChessException.class)
    @SendToUser("/queue/errors")
    public ErrorResponse handleChessException(ChessException ex) {
        return buildResponse(
            ex.getType(),
            ex.getMessage(),
            ex.getDetails()
        );
    }

    // ---------- Método Auxiliar ----------
    private ErrorResponse buildResponse(
        String type,
        String message,
        Map<String, Object> details
    ) {
        return new ErrorResponse(
            type,
            message,
            LocalDateTime.now(),
            "websocket://game-action",
            details
        );
    }
    
}
