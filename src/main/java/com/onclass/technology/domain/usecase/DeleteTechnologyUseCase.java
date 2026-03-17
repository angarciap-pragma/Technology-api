package com.onclass.technology.domain.usecase;

import reactor.core.publisher.Mono;

/**
 * Declara el caso de uso para eliminar tecnologias.
 */
public interface DeleteTechnologyUseCase {

    Mono<Void> deleteById(Long id);
}
