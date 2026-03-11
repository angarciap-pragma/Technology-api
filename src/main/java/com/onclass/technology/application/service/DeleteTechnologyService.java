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

        if (id == null || id <= 0) {
            return Mono.error(new ValidationException("Technology id must be greater than 0"));
        }

        return technologyRepositoryPort.existsById(id)
                .filter(Boolean.TRUE::equals)// si exists = true pasa el filtro
                .doOnNext(exists -> log.warn("Technology not found for deletion id={}", id))
                .switchIfEmpty(Mono.error(new NotFoundException("Technology not found")))// si no existe, lanzar error
                .flatMap(exists -> technologyRepositoryPort.deleteById(id))// si existe, ejecutar delete
                .doOnSuccess(unused -> log.info("Technology deleted id={}", id));
    }
}
