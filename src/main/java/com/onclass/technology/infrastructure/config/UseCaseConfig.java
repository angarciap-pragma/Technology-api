package com.onclass.technology.infrastructure.config;

import com.onclass.technology.application.service.CreateTechnologyService;
import com.onclass.technology.application.service.DeleteTechnologyService;
import com.onclass.technology.application.service.FindTechnologyByIdService;
import com.onclass.technology.domain.port.TechnologyRepositoryPort;
import com.onclass.technology.domain.usecase.CreateTechnologyUseCase;
import com.onclass.technology.domain.usecase.DeleteTechnologyUseCase;
import com.onclass.technology.domain.usecase.FindTechnologyByIdUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

/**
 * Declara beans necesarios para armar los casos de uso en tiempo de ejecucion.
 */
@Configuration
public class UseCaseConfig {

    @Bean
    public CreateTechnologyUseCase technologyServicePort(TechnologyRepositoryPort technologyRepositoryPort) {
        return new CreateTechnologyService(technologyRepositoryPort);
    }

    @Bean
    public FindTechnologyByIdUseCase findTechnologyByIdUseCase(TechnologyRepositoryPort technologyRepositoryPort) {
        return new FindTechnologyByIdService(technologyRepositoryPort);
    }

    @Bean
    public DeleteTechnologyUseCase deleteTechnologyUseCase(
            TechnologyRepositoryPort technologyRepositoryPort,
            TransactionalOperator transactionalOperator
    ) {
        return new DeleteTechnologyService(technologyRepositoryPort, transactionalOperator);
    }

    @Bean
    public TransactionalOperator transactionalOperator(ReactiveTransactionManager transactionManager) {
        return TransactionalOperator.create(transactionManager);
    }
}
