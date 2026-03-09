package com.onclass.technology.application.service;

import com.onclass.technology.domain.exception.NotFoundException;
import com.onclass.technology.domain.exception.ValidationException;
import com.onclass.technology.domain.model.Technology;
import com.onclass.technology.domain.port.TechnologyRepositoryPort;
import com.onclass.technology.domain.usecase.FindTechnologyByIdUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class FindTechnologyByIdService implements FindTechnologyByIdUseCase {

    private final TechnologyRepositoryPort technologyRepositoryPort;

    @Override
    public Mono<Technology> findById(Long id) {
        return Mono.defer(() -> {
            if (id == null || id <= 0) {
                throw new ValidationException("Technology id must be greater than 0");
            }

            return technologyRepositoryPort.findById(id)
                    .switchIfEmpty(Mono.error(new NotFoundException("Technology not found")));
        });
    }
}
