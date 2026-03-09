package com.onclass.technology.infrastructure.input.rest.mapper;

import com.onclass.technology.domain.model.Technology;
import com.onclass.technology.infrastructure.input.rest.dto.request.CreateTechnologyRequest;
import com.onclass.technology.infrastructure.input.rest.dto.response.TechnologyResponse;
import org.mapstruct.Mapper;

// Convierte entre DTOs HTTP y el modelo de dominio.
@Mapper(componentModel = "spring")
public interface TechnologyRestMapper {

    // Construye el agregado aplicando invariantes del dominio.
    default Technology toDomain(CreateTechnologyRequest request) {
        return Technology.create(request.name(), request.description());
    }

    TechnologyResponse toResponse(Technology technology);
}
