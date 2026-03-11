package com.onclass.technology.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;


/**
 * Configuración de OpenAPI que define los metadatos del microservicio
 * para la generación automática de documentación Swagger.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Technology API",
                version = "1.0.0",
                description = "Reactive microservice for technology registration",
                contact = @Contact(name = "On-Class")
        )
)
public class OpenApiConfig {
        // Clase de configuración utilizada únicamente para definir metadatos OpenAPI.
}

