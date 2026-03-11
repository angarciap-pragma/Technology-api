package com.onclass.technology.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onclass.technology.domain.exception.ConflictException;
import com.onclass.technology.domain.exception.ErrorCode;
import com.onclass.technology.domain.exception.NotFoundException;
import com.onclass.technology.domain.exception.ValidationException;
import com.onclass.technology.infrastructure.input.rest.dto.response.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * Maneja errores globales para WebFlux funcional.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {//exchange tiene contexto htpp request,response,headers,path
        HttpStatus status;
        ErrorCode errorCode;
        String message;

        // Determina tipo de excepción usando switch pattern matching (Java 21) / reemplaza if else
        switch (ex) {

            case ValidationException validationException -> {
                status = HttpStatus.BAD_REQUEST;
                errorCode = validationException.getErrorCode();
                message = validationException.getMessage();
            }

            case ConflictException conflictException -> {
                status = HttpStatus.CONFLICT;
                errorCode = conflictException.getErrorCode();
                message = conflictException.getMessage();
            }

            case NotFoundException notFoundException -> {
                status = HttpStatus.NOT_FOUND;
                errorCode = notFoundException.getErrorCode();
                message = notFoundException.getMessage();
            }

            default -> {
                status = HttpStatus.INTERNAL_SERVER_ERROR;
                errorCode = ErrorCode.INTERNAL_ERROR;
                message = "Unexpected server error";
            }
        }
        log.warn("Error on path='{}': {}", exchange.getRequest().getPath().value(), message);

        ErrorResponse errorResponse = new ErrorResponse(
                Instant.now(),
                status.value(),
                errorCode.name(),
                message,
                exchange.getRequest().getPath().value()
        );

        exchange.getResponse().setStatusCode(status);//define status del response
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);//define que el reponse es json

        return Mono.fromCallable(() -> objectMapper.writeValueAsBytes(errorResponse))
                .map(bytes -> exchange.getResponse().bufferFactory().wrap(bytes))
                .flatMap(buffer -> exchange.getResponse().writeWith(Mono.just(buffer)))
                .onErrorResume(error -> {
                    log.error("Error serializing error response", error);
                    return exchange.getResponse().setComplete();
                });
    }

}


/*
    Void porque no devolvemos un objeto Java, sino que escribimos directamente la respuesta HTTP.
    Throwable ex - captura el tipo de excepcion
   Mono.fromCallable(...) Esto crea un Mono que ejecuta una operación que puede lanzar error - evitra el try catch
   luego onErrorResume
   map → transformo datos
flatMap → llamo algo que devuelve Mono
onErrorResume → manejo error reactivo
fromCallable → ejecuto algo que puede lanzar excepción
*/
