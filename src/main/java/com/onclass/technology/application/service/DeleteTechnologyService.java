package com.onclass.technology.application.service;

import com.onclass.technology.domain.exception.NotFoundException;
import com.onclass.technology.domain.exception.ValidationException;
import com.onclass.technology.domain.port.TechnologyRepositoryPort;
import com.onclass.technology.domain.usecase.DeleteTechnologyUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class DeleteTechnologyService implements DeleteTechnologyUseCase {

    private final TechnologyRepositoryPort technologyRepositoryPort;

    @Override
    @Transactional
    public Mono<Void> deleteById(Long id) {
        return Mono.defer(() -> {
            log.debug("Starting deleteTechnologyById in application layer for id={}", id);
            if (id == null || id <= 0) {
                throw new ValidationException("Technology id must be greater than 0");
            }

            return technologyRepositoryPort.existsById(id)
                    .flatMap(exists -> {
                        if (!exists) {
                            return Mono.error(new NotFoundException("Technology not found"));
                        }
                        return technologyRepositoryPort.deleteById(id)
                                .doOnSuccess(unused -> log.info("Technology deleted id={}", id));
                    });
        });
    }
}
