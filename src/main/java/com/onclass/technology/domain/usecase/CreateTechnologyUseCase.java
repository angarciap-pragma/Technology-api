package com.onclass.technology.domain.usecase;

import com.onclass.technology.domain.model.Technology;
import reactor.core.publisher.Mono;

/**
 * Declara el caso de uso para registrar tecnologias.
 */
public interface CreateTechnologyUseCase {

    Mono<Technology> createTechnology(Technology technology);
}

