package com.chess.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.chess.api.dto.response.ErrorResponse;
import com.chess.domain.exception.ChessException;

import io.jsonwebtoken.lang.Collections;
import jakarta.servlet.http.HttpServletRequest;
import tools.jackson.databind.exc.InvalidFormatException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalHttpExceptionHandler {

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
    @ExceptionHandler(ChessException.class)
    public ResponseEntity<ErrorResponse> handleChessException(
        ChessException ex,
        HttpServletRequest request
    ) {
        return buildResponse(
            ex.getStatus(),
            ex.getType(),
            ex, request,
            ex.getDetails());
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
        return buildResponse(status, type, ex, request, Collections.emptyMap());
    }

}
