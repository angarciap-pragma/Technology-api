package com.onclass.technology.infrastructure.input.rest.handler;

import com.onclass.technology.domain.usecase.CreateTechnologyUseCase;
import com.onclass.technology.domain.usecase.DeleteTechnologyUseCase;
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
    private final DeleteTechnologyUseCase deleteTechnologyUseCase;
    private final FindTechnologyByIdUseCase findTechnologyByIdUseCase;
    private final TechnologyRestMapper technologyRestMapper;
    private final Validator validator;

    // Registra una nueva tecnologia aplicando validaciones de entrada y dominio.
    public Mono<ServerResponse> createTechnology(ServerRequest request) {
        log.info("HTTP POST {} - starting technology creation", request.path());
        return request.bodyToMono(CreateTechnologyRequest.class)//convierte body json a objeto
                .switchIfEmpty(Mono.error(new ValidationException("Payload is required")))
                .doOnNext(this::validateRequest)//ejecuta validaciones
                .doOnNext(validRequest -> log.info("Creating technology with name='{}'", validRequest.name()))//solo ejecuta efectos secundarios
                .map(technologyRestMapper::toDomain)//convierte rest a dominio, se usa map porque no devuelve mono
                .flatMap(createTechnologyUseCase::createTechnology)//ejecuta caso de uso, devuelve mono
                .map(technologyRestMapper::toResponse)//convierte dominio a response, .map no devuelve mono
                .doOnNext(response -> log.info("Technology created successfully with id={}", response.id()))//logs
                .flatMap(response -> ServerResponse.status(201)//construye respuesta http
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response)
                ).doOnError(error -> log.error("Error creating technology: {}", error.getMessage()));
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

    // Ejecuta validaciones de bean validation sobre el request / xq no se ejecuta el @valid de spring con routerFuntion, handler, ServerRequest
    private void validateRequest(CreateTechnologyRequest request) {
        Set<ConstraintViolation<CreateTechnologyRequest>> violations = validator.validate(request); //se usa Bean Validation (Jakarta Validation) //ConstraintViolation contiene todos los errores de validacion del dto
        if (!violations.isEmpty()) {//si no hay errores/ vacio
            String message = violations.iterator().next().getMessage();//si hay errores,se toma el primer error
            throw new ValidationException(message);//Captura el error
        }
    }

    private Long parseTechnologyId(String id) {
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException exception) {
            throw new ValidationException("Technology id must be numeric");
        }
    }
}

/*
âœ” no bloquea hilos
âœ” soporta muchas peticiones
âœ” mejor escalabilidad
âœ” en lugar de thread por request, usa event loop + async
 Mono pipeline=request - map(mapear) - flapMap(validar) - map(guardar) - response
 map â†’ transforma objeto
 flatMap â†’ llama algo que devuelve Mono
 doOnNext â†’ efecto secundario (log)
 switchIfEmpty â†’ manejo de vacÃ­o

 operadores mas importantes
 map - solo cuando se transforma a objeto, no hay operaciones asincronicas - transforma datos
 flapMap - cuando la funcion devuelve mono o flux - llama operacion async
 concatMap - similar al flapMap pero ejecuta en orden uno*uno - garantiza orden
 flapMap con concurrencia - puede ejcutar en paralelo
 switchMap - Cancela el flujo anterior y usa solo el Ãºltimo.
 */
