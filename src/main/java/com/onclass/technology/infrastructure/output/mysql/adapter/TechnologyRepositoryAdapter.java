package com.onclass.technology.infrastructure.output.mysql.adapter;

import com.onclass.technology.domain.model.Technology;
import com.onclass.technology.domain.port.TechnologyRepositoryPort;
import com.onclass.technology.infrastructure.output.mysql.mapper.TechnologyEntityMapper;
import com.onclass.technology.infrastructure.output.mysql.repository.TechnologyReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Implementa el puerto de persistencia del dominio usando MySQL reactivo.
 **/

@Component
@Slf4j
@RequiredArgsConstructor
public class TechnologyRepositoryAdapter implements TechnologyRepositoryPort {

    private final TechnologyReactiveRepository technologyReactiveRepository;
    private final TechnologyEntityMapper technologyEntityMapper;

    @Override
    public Mono<Boolean> existsByNormalizedName(String normalizedName) {
        log.debug("Checking technology existence by normalizedName='{}'", normalizedName);
        return technologyReactiveRepository.existsByNormalizedName(normalizedName);// Ejecuta consulta reactiva asegurando formato normalizado.
    }

    @Override
    public Mono<Technology> save(Technology technology) {
        return technologyReactiveRepository.save(technologyEntityMapper.toEntity(technology))// Convierte el dominio a entidad para repositorio.
                .map(technologyEntityMapper::toDomain)// Convierte la entidad guardada de vuelta al dominiO / entra un objeto, sale otro, .map no devuelve mono
                .doOnSuccess(saved -> log.info("Technology persisted = {}", saved));
    }

    @Override
    public Mono<Technology> findById(Long id) {
        return technologyReactiveRepository.findById(id)
                .map(technologyEntityMapper::toDomain);
    }

    @Override
    public Mono<Boolean> existsById(Long id) {
        return technologyReactiveRepository.existsById(id);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return technologyReactiveRepository.deleteById(id);
    }
}
