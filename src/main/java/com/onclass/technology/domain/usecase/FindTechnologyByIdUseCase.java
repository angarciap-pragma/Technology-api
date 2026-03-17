package com.onclass.technology.domain.usecase;

import com.onclass.technology.domain.model.Technology;
import reactor.core.publisher.Mono;

/**
 * Declara el caso de uso para consultar una tecnologia por id.
 */
public interface FindTechnologyByIdUseCase {

    Mono<Technology> findById(Long id);
}
