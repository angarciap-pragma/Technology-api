package com.onclass.technology.infrastructure.input.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Representa el payload HTTP requerido para registrar una tecnologia.
 */
public record CreateTechnologyRequest(
        @NotBlank(message = "Technology name is required")
        @Size(max = 50, message = "Technology name exceeds 50 characters")
        @Schema(
                description = "Technology name. Business rule: max 50 characters and cannot be blank",
                example = "Java"
        )
        String name,
        @NotBlank(message = "Technology description is required")
        @Size(max = 90, message = "Technology description exceeds 90 characters")
        @Schema(
                description = "Technology description. Business rule: max 90 characters and cannot be blank",
                example = "Backend programming language"
        )
        String description
) {}

