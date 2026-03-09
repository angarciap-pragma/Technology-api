package com.onclass.technology.domain.usecase;

import com.onclass.technology.domain.model.Technology;
import reactor.core.publisher.Mono;

public interface CreateTechnologyUseCase {

    Mono<Technology> createTechnology(Technology technology);
}

