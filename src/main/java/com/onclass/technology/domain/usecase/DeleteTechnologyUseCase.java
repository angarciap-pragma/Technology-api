package com.onclass.technology.domain.usecase;

import reactor.core.publisher.Mono;

public interface DeleteTechnologyUseCase {

    Mono<Void> deleteById(Long id);
}
