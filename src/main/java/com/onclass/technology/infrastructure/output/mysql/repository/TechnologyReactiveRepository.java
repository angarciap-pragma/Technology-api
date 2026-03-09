package com.onclass.technology.infrastructure.output.mysql.repository;

import com.onclass.technology.infrastructure.output.mysql.entity.TechnologyEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface TechnologyReactiveRepository extends ReactiveCrudRepository<TechnologyEntity, Long> {

    Mono<Boolean> existsByNormalizedName(String normalizedName);
}

