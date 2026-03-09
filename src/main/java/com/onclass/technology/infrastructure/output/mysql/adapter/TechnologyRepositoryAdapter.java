package com.onclass.technology.infrastructure.output.mysql.adapter;

import com.onclass.technology.domain.model.Technology;
import com.onclass.technology.domain.port.TechnologyRepositoryPort;
import com.onclass.technology.infrastructure.output.mysql.mapper.TechnologyEntityMapper;
import com.onclass.technology.infrastructure.output.mysql.repository.TechnologyReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Locale;

// Implementa el puerto de persistencia del dominio usando MySQL reactivo.
@Component
@Slf4j
@RequiredArgsConstructor
public class TechnologyRepositoryAdapter implements TechnologyRepositoryPort {

    private final TechnologyReactiveRepository technologyReactiveRepository;
    private final TechnologyEntityMapper technologyEntityMapper;

    // Consulta si existe una tecnologia con ese nombre normalizado.
    @Override
    public Mono<Boolean> existsByNormalizedName(String normalizedName) {
        log.debug("Checking technology existence by normalizedName='{}'", normalizedName);
        // Ejecuta consulta reactiva asegurando formato normalizado.
        return technologyReactiveRepository.existsByNormalizedName(normalizedName.toLowerCase(Locale.ROOT));
    }

    // Persiste una tecnologia en la base de datos.
    @Override
    public Mono<Technology> save(Technology technology) {
        log.debug("Persisting technology with name='{}'", technology.getName());
        // Convierte el dominio a entidad para repositorio.
        return technologyReactiveRepository.save(technologyEntityMapper.toEntity(technology))
                // Convierte la entidad guardada de vuelta al dominio.
                .map(technologyEntityMapper::toDomain)
                .doOnSuccess(saved -> log.info("Technology persisted with id={}", saved.getId()));
    }
}
