package com.onclass.technology.domain.port;

import com.onclass.technology.domain.model.Technology;
import reactor.core.publisher.Mono;

public interface TechnologyRepositoryPort {

    Mono<Technology> save(Technology technology);

    Mono<Boolean> existsByNormalizedName(String normalizedName);

    Mono<Technology> findById(Long id);

    Mono<Boolean> existsById(Long id);

    Mono<Void> deleteById(Long id);

}
