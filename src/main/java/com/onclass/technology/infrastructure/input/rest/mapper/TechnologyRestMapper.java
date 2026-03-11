package com.onclass.technology.infrastructure.input.rest.mapper;

import com.onclass.technology.domain.model.Technology;
import com.onclass.technology.infrastructure.input.rest.dto.request.CreateTechnologyRequest;
import com.onclass.technology.infrastructure.input.rest.dto.response.TechnologyResponse;
import org.mapstruct.Mapper;

// Convierte entre DTOs HTTP y el modelo de dominio. evita que el dominio conozca cosas de rest
@Mapper(componentModel = "spring")//mapper le dice a mapStructure que genere impl / spring crea bean
public interface TechnologyRestMapper { //interfaz xq mapStructure genera impl

    // Construye el agregado aplicando invariantes del dominio.
    default Technology toDomain(CreateTechnologyRequest request) {//default xq mapStructure no genera el metodo automatico
        return Technology.create(request.name(), request.description());//factory method - crea objetos en lugar de constructor
    }

    TechnologyResponse toResponse(Technology technology);
}
