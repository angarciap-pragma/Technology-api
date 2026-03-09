package com.onclass.technology.application.service;

import com.onclass.technology.domain.usecase.CreateTechnologyUseCase;
import com.onclass.technology.domain.exception.ConflictException;
import com.onclass.technology.domain.exception.ValidationException;
import com.onclass.technology.domain.model.Technology;
import com.onclass.technology.domain.port.TechnologyRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Locale;

@RequiredArgsConstructor
public class CreateCreateTechnologyService implements CreateTechnologyUseCase {

    private final TechnologyRepositoryPort technologyRepositoryPort;

    // Ejecuta el registro de tecnologia aplicando validaciones y unicidad.
    @Override
    public Mono<Technology> createTechnology(Technology technology) {
        // Envuelve la logica para que errores de validacion sean reactivos.
        return Mono.defer(() -> {
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
                    // Evalua el resultado de existencia para decidir flujo.
                    .flatMap(exists -> {
                        // Si ya existe, retorna error de nombre duplicado.
                        if (exists) {
                            return Mono.error(new ConflictException("Technology name already exists"));
                        }
                        // Si no existe, guarda la nueva tecnologia.
                        return technologyRepositoryPort.save(technology);
                    });
        });
    }

    // Normaliza el nombre para aplicar unicidad sin depender de formato.
    private String normalizeName(String name) {
        // Convierte el nombre a minusculas usando locale estable.
        return name.toLowerCase(Locale.ROOT);
    }
}
