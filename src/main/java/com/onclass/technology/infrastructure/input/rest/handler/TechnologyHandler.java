package com.onclass.technology.infrastructure.input.rest.handler;

import com.onclass.technology.domain.exception.ValidationException;
import com.onclass.technology.domain.usecase.CreateTechnologyUseCase;
import com.onclass.technology.domain.usecase.DeleteTechnologyUseCase;
import com.onclass.technology.domain.usecase.FindTechnologyByIdUseCase;
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

import java.util.stream.Collectors;

/**
 * Gestiona las peticiones HTTP reactivas del modulo de tecnologias.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class TechnologyHandler {

    private final CreateTechnologyUseCase createTechnologyUseCase;
    private final DeleteTechnologyUseCase deleteTechnologyUseCase;
    private final FindTechnologyByIdUseCase findTechnologyByIdUseCase;
    private final TechnologyRestMapper technologyRestMapper;
    private final Validator validator;

    // Registra una nueva tecnologia aplicando validaciones de entrada y dominio.
    public Mono<ServerResponse> createTechnology(ServerRequest request) {
        log.info("HTTP POST {} - starting technology creation", request.path());
        return request.bodyToMono(CreateTechnologyRequest.class) // Lee el cuerpo HTTP y lo convierte a CreateTechnologyRequest.
                .switchIfEmpty(Mono.error(new ValidationException("Payload is required"))) // Si el cuerpo viene vacio, responde con error de validacion.
                .doOnNext(this::validateRequest) // Ejecuta validaciones del DTO antes de continuar el flujo.
                .doOnNext(validRequest -> log.info("Creating technology with name='{}'", validRequest.name()))
                .map(technologyRestMapper::toDomain) // Convierte el DTO de entrada al modelo de dominio Technology.
                .flatMap(createTechnologyUseCase::createTechnology) // Ejecuta el caso de uso para crear la tecnologia.
                .map(technologyRestMapper::toResponse)
                .doOnNext(response -> log.info("Technology created successfully with id={}", response.id()))
                .flatMap(response -> ServerResponse.status(201)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response))
                .doOnError(error -> log.error("Error creating technology: {}", error.getMessage()));
    }

    // Obtiene una tecnologia por id.
    public Mono<ServerResponse> findTechnologyById(ServerRequest request) {
        Long technologyId = parseTechnologyId(request.pathVariable("id"));
        log.info("HTTP GET {} - finding technology by id={}", request.path(), technologyId);
        return findTechnologyByIdUseCase.findById(technologyId)
                .map(technologyRestMapper::toResponse)
                .doOnNext(response -> log.info("Technology found for id={} with name='{}'", response.id(), response.name()))
                .doOnError(error -> log.error("Error finding technology by id={}: {}", technologyId, error.getMessage()))
                .flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response));
    }

    // Elimina una tecnologia por id.
    public Mono<ServerResponse> deleteTechnologyById(ServerRequest request) {
        Long technologyId = parseTechnologyId(request.pathVariable("id"));
        log.info("HTTP DELETE {} - deleting technology by id={}", request.path(), technologyId);
        return deleteTechnologyUseCase.deleteById(technologyId)
                .doOnSuccess(unused -> log.info("Technology deletion completed id={}", technologyId))
                .doOnError(error -> log.error("Error deleting technology by id={}: {}", technologyId, error.getMessage()))
                .then(ServerResponse.noContent().build());
    }

    private Long parseTechnologyId(String id) { // Convierte el id del path a un numero Long.
        try {
            return Long.parseLong(id); // Intenta transformar el texto recibido a Long.
        } catch (NumberFormatException exception) { // Captura el error si el texto no es numerico.
            throw new ValidationException("Technology id must be numeric"); // Lanza una excepcion de validacion con mensaje claro.
        }
    }

    private void validateRequest(CreateTechnologyRequest request) {
        var violations = validator.validate(request); // Ejecuta Bean Validation sobre el request y obtiene las reglas incumplidas.
        if (!violations.isEmpty()) { // Verifica si existe al menos una violación de validación.
            String message = violations.stream() // Convierte el conjunto de violaciones en un flujo para procesarlo.
                    .map(ConstraintViolation::getMessage) // Extrae el mensaje de error de cada violación encontrada.
                    .sorted() // Ordena alfabéticamente los mensajes para que la salida sea consistente.
                    .collect(Collectors.joining(", ")); // Une todos los mensajes en un solo texto separado por comas.
            throw new ValidationException(message); // Lanza una excepción de validación con el detalle consolidado.
        }
    }
}
