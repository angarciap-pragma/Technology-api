package com.onclass.technology.infrastructure.input.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateTechnologyRequest(
        @Schema(
                description = "Technology name. Business rule: max 50 characters and cannot be blank",
                example = "Java"
        )
        String name,
        @Schema(
                description = "Technology description. Business rule: max 90 characters and cannot be blank",
                example = "Backend programming language"
        )
        String description
) {}

