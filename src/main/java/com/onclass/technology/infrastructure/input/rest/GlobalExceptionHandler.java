package com.onclass.technology.infrastructure.input.rest;

import com.onclass.technology.domain.exception.ConflictException;
import com.onclass.technology.domain.exception.ErrorCode;
import com.onclass.technology.domain.exception.NotFoundException;
import com.onclass.technology.domain.exception.ValidationException;
import com.onclass.technology.infrastructure.input.rest.dto.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;

import java.time.Instant;

// Centraliza el manejo de excepciones para respuestas de error uniformes.
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Maneja errores de validacion funcional del dominio.
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            ValidationException exception,
            ServerWebExchange exchange
    ) {
        log.warn("Validation error on path='{}': {}", exchange.getRequest().getPath().value(), exception.getMessage());
        // Crea el cuerpo de error con informacion de trazabilidad.
        ErrorResponse response = new ErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                exception.getErrorCode().name(),
                exception.getMessage(),
                exchange.getRequest().getPath().value()
        );
        // Retorna la respuesta con status 400.
        return ResponseEntity.badRequest().body(response);
    }

    // Maneja errores cuando el nombre de tecnologia ya existe.
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(
            ConflictException exception,
            ServerWebExchange exchange
    ) {
        log.warn("Conflict error on path='{}': {}", exchange.getRequest().getPath().value(), exception.getMessage());
        // Crea el cuerpo de error para conflicto de unicidad.
        ErrorResponse response = new ErrorResponse(
                Instant.now(),
                HttpStatus.CONFLICT.value(),
                exception.getErrorCode().name(),
                exception.getMessage(),
                exchange.getRequest().getPath().value()
        );
        // Retorna la respuesta con status 409.
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // Maneja conflictos de unicidad disparados directamente desde la base de datos.
    @ExceptionHandler({DuplicateKeyException.class, DataIntegrityViolationException.class})
    public ResponseEntity<ErrorResponse> handleDataIntegrityConflict(
            RuntimeException exception,
            ServerWebExchange exchange
    ) {
        log.warn("Data integrity conflict on path='{}': {}", exchange.getRequest().getPath().value(), exception.getMessage());
        ErrorResponse response = new ErrorResponse(
                Instant.now(),
                HttpStatus.CONFLICT.value(),
                ErrorCode.CONFLICT_ERROR.name(),
                "Technology name already exists",
                exchange.getRequest().getPath().value()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // Maneja errores cuando un recurso no existe.
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(
            NotFoundException exception,
            ServerWebExchange exchange
    ) {
        log.warn("Not found error on path='{}': {}", exchange.getRequest().getPath().value(), exception.getMessage());
        ErrorResponse response = new ErrorResponse(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                exception.getErrorCode().name(),
                exception.getMessage(),
                exchange.getRequest().getPath().value()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Maneja errores de validacion de anotaciones del request HTTP.
    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ErrorResponse> handleWebExchangeBindException(
            WebExchangeBindException exception,
            ServerWebExchange exchange
    ) {
        // Obtiene el primer mensaje de validacion o usa mensaje generico.
        String message = exception.getFieldErrors().isEmpty()
                ? "Invalid request payload"
                : exception.getFieldErrors().get(0).getDefaultMessage();
        log.warn("Request payload validation error on path='{}': {}", exchange.getRequest().getPath().value(), message);

        // Crea el cuerpo de error para payload invalido.
        ErrorResponse response = new ErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                ErrorCode.INVALID_REQUEST_PAYLOAD.name(),
                message,
                exchange.getRequest().getPath().value()
        );
        // Retorna la respuesta con status 400.
        return ResponseEntity.badRequest().body(response);
    }
}
