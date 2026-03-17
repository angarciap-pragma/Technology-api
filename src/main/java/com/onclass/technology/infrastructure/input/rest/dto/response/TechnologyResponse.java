package com.onclass.technology.infrastructure.input.rest.dto.response;

/**
 * Representa la respuesta HTTP devuelta al registrar o consultar una tecnologia.
 */
public record TechnologyResponse(Long id, String name, String description) {

}

