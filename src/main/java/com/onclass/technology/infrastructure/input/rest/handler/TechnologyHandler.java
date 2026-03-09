package com.onclass.technology.infrastructure.input.rest.handler;

import com.onclass.technology.domain.usecase.CreateTechnologyUseCase;
import com.onclass.technology.domain.usecase.FindTechnologyByIdUseCase;
import com.onclass.technology.domain.exception.ValidationException;
import com.onclass.technology.infrastructure.input.rest.dto.request.CreateTechnologyRequest;
import com.onclass.technology.infrastructure.input.rest.mapper.TechnologyRestMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class TechnologyHandler {

    private final CreateTechnologyUseCase createTechnologyUseCase;
    private final FindTechnologyByIdUseCase findTechnologyByIdUseCase;
    private final TechnologyRestMapper technologyRestMapper;
    private final Validator validator;

    // Registra una nueva tecnologia aplicando validaciones de entrada y dominio.
    public Mono<ServerResponse> createTechnology(ServerRequest request) {
        return request.bodyToMono(CreateTechnologyRequest.class)
                .switchIfEmpty(Mono.error(new ValidationException("Technology payload is required")))
                .flatMap(this::validateRequest)
                .doOnNext(validRequest -> log.info("Creating technology with name='{}'", validRequest.name()))
                .map(technologyRestMapper::toDomain)
                .flatMap(createTechnologyUseCase::createTechnology)
                .map(technologyRestMapper::toResponse)
                .doOnNext(response -> log.info("Technology created successfully with id={}", response.id()))
                .flatMap(response -> ServerResponse.status(201)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response));
    }

    // Obtiene una tecnologia por id.
    public Mono<ServerResponse> findTechnologyById(ServerRequest request) {
        Long technologyId = parseTechnologyId(request.pathVariable("id"));
        return findTechnologyByIdUseCase.findById(technologyId)
                .map(technologyRestMapper::toResponse)
                .flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response));
    }

    // Ejecuta validaciones de bean validation sobre el request.
    private Mono<CreateTechnologyRequest> validateRequest(CreateTechnologyRequest request) {
        Set<ConstraintViolation<CreateTechnologyRequest>> violations = validator.validate(request);
        if (violations.isEmpty()) {
            return Mono.just(request);
        }
        String message = violations.iterator().next().getMessage();
        return Mono.error(new ValidationException(message));
    }

    private Long parseTechnologyId(String id) {
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException exception) {
            throw new ValidationException("Technology id must be numeric");
        }
    }
}
