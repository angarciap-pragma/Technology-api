package com.onclass.technology.infrastructure.config;

import com.onclass.technology.domain.usecase.CreateTechnologyUseCase;
import com.onclass.technology.domain.port.TechnologyRepositoryPort;
import com.onclass.technology.application.service.CreateCreateTechnologyService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Declara beans necesarios para armar el caso de uso en tiempo de ejecucion.
@Configuration
public class UseCaseConfig {

    // Expone el servicio de dominio con su puerto de persistencia conectado.
    @Bean
    public CreateTechnologyUseCase technologyServicePort(TechnologyRepositoryPort technologyRepositoryPort) {
        // Retorna una instancia del caso de uso principal.
        return new CreateCreateTechnologyService(technologyRepositoryPort);
    }
}

