package com.chess.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.chess.api.dto.response.ErrorResponse;
import com.chess.domain.exception.game.GameNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalApiExceptionHandler {

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
            Map.of("details", fieldErrors));
    }

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
