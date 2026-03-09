package com.onclass.technology.application.service;

import com.onclass.technology.domain.usecase.CreateTechnologyUseCase;
import com.onclass.technology.domain.exception.ConflictException;
import com.onclass.technology.domain.exception.ValidationException;
import com.onclass.technology.domain.model.Technology;
import com.onclass.technology.domain.port.TechnologyRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
public class CreateCreateTechnologyService implements CreateTechnologyUseCase {

    private final TechnologyRepositoryPort technologyRepositoryPort;

    // Ejecuta el registro de tecnologia aplicando validaciones y unicidad.
    @Override
    public Mono<Technology> createTechnology(Technology technology) {
        // Envuelve la logica para que errores de validacion sean reactivos.
        return Mono.defer(() -> {
            log.debug("Starting createTechnology in application layer");
            if (technology == null) {
                throw new ValidationException("Technology payload is required");
            }
            // Calcula nombre normalizado para validar unicidad sin mayusculas.
            String normalizedName = normalizeName(technology.getName());
            // Verifica que en creacion no se reciba id.
            if (technology.getId() != null) {
                throw new ValidationException("Technology id must be null for creation");
            }

            // Valida que no exista el nombre y persiste si esta disponible.
            return technologyRepositoryPort.existsByNormalizedName(normalizedName)
                    .doOnNext(exists -> log.debug("Uniqueness check for normalizedName='{}': exists={}", normalizedName, exists))
                    // Evalua el resultado de existencia para decidir flujo.
                    .flatMap(exists -> {
                        // Si ya existe, retorna error de nombre duplicado.
                        if (exists) {
                            log.warn("Business conflict: duplicated technology name '{}'", technology.getName());
                            return Mono.error(new ConflictException("Technology name already exists"));
                        }
                        // Si no existe, guarda la nueva tecnologia.
                        return technologyRepositoryPort.save(technology)
                                .doOnSuccess(saved -> log.info("Technology persisted from application layer with id={}", saved.getId()));
                    });
        });
    }

    // Normaliza el nombre para aplicar unicidad sin depender de formato.
    private String normalizeName(String name) {
        // Convierte el nombre a minusculas usando locale estable.
        return name.toLowerCase(Locale.ROOT);
    }
}
