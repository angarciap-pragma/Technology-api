package com.onclass.technology.application.service;

import com.onclass.technology.domain.usecase.CreateTechnologyUseCase;
import com.onclass.technology.domain.exception.ConflictException;
import com.onclass.technology.domain.model.Technology;
import com.onclass.technology.domain.port.TechnologyRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class CreateTechnologyService implements CreateTechnologyUseCase {

    private final TechnologyRepositoryPort technologyRepositoryPort;

    @Override
    public Mono<Technology> createTechnology(Technology technology) {
            return technologyRepositoryPort.existsByNormalizedName(technology.normalizedName())//Hace una consulta reactiva al repositorio, para nombre normalizado
                    .filter(Boolean.TRUE::equals)// si exists = true pasa el filtro
                    .doOnNext(exist -> log.warn("Duplicated technology name '{}'", technology.getName()))
                    .flatMap(exists -> Mono.<Technology>error(new ConflictException("Technology name already exists")))// si pasó el filtro (ya existe), lanzar error
                    .switchIfEmpty(// si no pasó el filtro (exists=false), se ejecuta el save
                            technologyRepositoryPort.save(technology)
                                    .doOnSuccess(saved ->
                                            log.info("Technology persisted successfully with name={}", saved.getName())
                                    )
                    );
    }

}

/*
 * filter	deja pasar solo los valores que cumplen condición
 * flatMap	transforma el flujo en otro Mono
 * switchIfEmpty	ejecuta lógica cuando el flujo queda vacío
 * doOnSuccess	ejecuta efecto secundario (log)
*/
