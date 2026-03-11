package com.onclass.technology.infrastructure.output.mysql.repository;

import com.onclass.technology.infrastructure.output.mysql.entity.TechnologyEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

/**
 * Repositorio de infra que habla con la base de datos
 * Extiende ReactiveCrudRepository, hace consultas reales a MySQL - interfaz de spring data
 * event loop usa R2DBC
 * **/

public interface TechnologyReactiveRepository extends ReactiveCrudRepository<TechnologyEntity, Long> {

    Mono<Boolean> existsByNormalizedName(String normalizedName);
}

