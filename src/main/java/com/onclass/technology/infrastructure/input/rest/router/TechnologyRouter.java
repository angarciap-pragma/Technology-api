package com.onclass.technology.infrastructure.input.rest.router;

import com.onclass.technology.infrastructure.input.rest.handler.TechnologyHandler;
import com.onclass.technology.infrastructure.input.rest.routes.TechnologyRoutes;
import com.onclass.technology.infrastructure.input.rest.dto.response.ErrorResponse;
import com.onclass.technology.infrastructure.input.rest.dto.response.TechnologyResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;

// Configura rutas funcionales WebFlux del modulo de tecnologias.
@Configuration
public class TechnologyRouter {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = TechnologyRoutes.BASE_PATH,
                    method = RequestMethod.POST,
                    operation = @Operation(
                            operationId = "createTechnology",
                            summary = "Register technology",
                            description = "Registers a technology to be used by bootcamp capabilities",
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Technology created",
                                            content = @Content(schema = @Schema(implementation = TechnologyResponse.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Invalid payload",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "409",
                                            description = "Technology name already exists",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> technologyRoutes(TechnologyHandler technologyHandler) {
        return RouterFunctions
                .route(POST(TechnologyRoutes.BASE_PATH),
                 technologyHandler::createTechnology);
    }
}
