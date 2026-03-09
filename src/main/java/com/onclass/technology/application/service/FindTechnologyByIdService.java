package com.onclass.technology.application.service;

import com.onclass.technology.domain.exception.NotFoundException;
import com.onclass.technology.domain.exception.ValidationException;
import com.onclass.technology.domain.model.Technology;
import com.onclass.technology.domain.port.TechnologyRepositoryPort;
import com.onclass.technology.domain.usecase.FindTechnologyByIdUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class FindTechnologyByIdService implements FindTechnologyByIdUseCase {

    private final TechnologyRepositoryPort technologyRepositoryPort;

    @Override
    public Mono<Technology> findById(Long id) {
        return Mono.defer(() -> {
            log.debug("Starting findTechnologyById in application layer for id={}", id);
            if (id == null || id <= 0) {
                throw new ValidationException("Technology id must be greater than 0");
            }

            return technologyRepositoryPort.findById(id)
                    .doOnNext(technology -> log.info("Technology loaded from repository id={}", technology.getId()))
                    .switchIfEmpty(Mono.error(new NotFoundException("Technology not found")));
        });
    }
}
