package com.onclass.technology.infrastructure.input.rest.dto.response;

import java.time.Instant;

/**
 * Representa el contrato uniforme de error expuesto por el microservicio.
 */
public record ErrorResponse(Instant timestamp, int status, String code, String message, String path) {

}
