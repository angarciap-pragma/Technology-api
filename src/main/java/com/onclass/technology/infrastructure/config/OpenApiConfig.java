package com.onclass.technology.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;


/**
 * ConfiguraciÃ³n de OpenAPI que define los metadatos del microservicio
 * para la generaciÃ³n automÃ¡tica de documentaciÃ³n Swagger.
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
        // Clase de configuraciÃ³n utilizada Ãºnicamente para definir metadatos OpenAPI.
}

