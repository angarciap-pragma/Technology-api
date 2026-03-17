package com.onclass.technology.application.service;

import com.onclass.technology.domain.exception.ConflictException;
import com.onclass.technology.domain.exception.ValidationException;
import com.onclass.technology.domain.model.Technology;
import com.onclass.technology.domain.port.TechnologyRepositoryPort;
import com.onclass.technology.domain.usecase.CreateTechnologyUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Implementa el caso de uso encargado de registrar tecnologias nuevas.
 */
@Slf4j
@RequiredArgsConstructor
public class CreateTechnologyService implements CreateTechnologyUseCase {

    private final TechnologyRepositoryPort technologyRepositoryPort;

    @Override
    public Mono<Technology> createTechnology(Technology technology) {
        if (technology == null) {//redundante to do
            return Mono.error(new ValidationException("Technology payload is required"));
        }

            return technologyRepositoryPort.existsByNormalizedName(technology.normalizedName()) // Consulta en persistencia si ya existe una tecnologia con el nombre normalizado.
                .filter(Boolean.TRUE::equals) // Deja pasar solo el caso en el que la respuesta sea true; si es false, el flujo queda vacio.
                .doOnNext(exist -> log.warn("Duplicated technology name '{}'", technology.getName())) // Registra en logs que se detectó un nombre duplicado.
                .flatMap(exists -> Mono.<Technology>error(new ConflictException("Technology name already exists"))) // Si ya existe, transforma el flujo en un error de conflicto 409.
                .switchIfEmpty( // Si el flujo quedó vacío, significa que la tecnologia no existe y se puede guardar.
                        Mono.defer(() -> technologyRepositoryPort.save(technology) // Difere la operación de guardado hasta que realmente se necesite ejecutar.
                                .doOnSuccess(saved -> log.info("Technology persisted successfully with name={}", saved.getName()))) // Registra en logs que la tecnologia fue guardada correctamente.
                );
    }
}
