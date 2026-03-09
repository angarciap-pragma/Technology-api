package com.onclass.technology.infrastructure.input.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTechnologyRequest(
        @NotBlank @Size(max = 50) String name,
        @NotBlank @Size(max = 90) String description
) {}

