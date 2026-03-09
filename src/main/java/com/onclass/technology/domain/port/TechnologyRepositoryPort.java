package com.onclass.technology.domain.port;

import com.onclass.technology.domain.model.Technology;
import reactor.core.publisher.Mono;

public interface TechnologyRepositoryPort {

    Mono<Technology> save(Technology technology);

    // Consulta si ya existe una tecnologia con el nombre normalizado.
    Mono<Boolean> existsByNormalizedName(String normalizedName);

}

