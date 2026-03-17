package com.onclass.technology.application.service;

import com.onclass.technology.domain.exception.NotFoundException;
import com.onclass.technology.domain.exception.ValidationException;
import com.onclass.technology.domain.port.TechnologyRepositoryPort;
import com.onclass.technology.domain.usecase.DeleteTechnologyUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

/**
 * Implementa el caso de uso encargado de eliminar tecnologias existentes.
 */
@Slf4j
@RequiredArgsConstructor
public class DeleteTechnologyService implements DeleteTechnologyUseCase {

    private final TechnologyRepositoryPort technologyRepositoryPort;
    private final TransactionalOperator transactionalOperator;

    @Override
    public Mono<Void> deleteById(Long id) {

        if (id == null || id <= 0) {
            return Mono.error(new ValidationException("Technology id must be greater than 0"));
        }

        return technologyRepositoryPort.existsById(id)
                .filter(Boolean.TRUE::equals)
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Technology not found for deletion id={}", id);
                    return Mono.error(new NotFoundException("Technology not found"));
                }))
                .flatMap(exists -> technologyRepositoryPort.deleteById(id))
                .as(transactionalOperator::transactional)
                .doOnSuccess(unused -> log.info("Technology deleted id={}", id));
    }
}
