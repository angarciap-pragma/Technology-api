package com.onclass.technology.infrastructure.output.mysql.mapper;

import com.onclass.technology.domain.model.Technology;
import com.onclass.technology.infrastructure.output.mysql.entity.TechnologyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Locale;

/** Convierte entre el modelo de dominio y la entidad de infraestructura. Usa liberia mapStructure**/

@Mapper(componentModel = "spring")
public interface TechnologyEntityMapper {

    @Mapping(target = "normalizedName", expression = "java(normalizeName(technology.getName()))")//le dice a MapStructure como llenar el campo, xq no existe en el dominio, solo en db
    TechnologyEntity toEntity(Technology technology);

    default Technology toDomain(TechnologyEntity entity) {
        return Technology.rehydrate(entity.getId(), entity.getName(), entity.getDescription());//usa el factory method del dominio
    }

    // Normaliza el nombre para validaciones de unicidad.
    default String normalizeName(String name) {
        return name.toLowerCase(Locale.ROOT);
    }
}
