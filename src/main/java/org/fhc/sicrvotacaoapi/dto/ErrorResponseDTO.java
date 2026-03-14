package org.fhc.sicrvotacaoapi.dto;

import java.util.Map;

public record ErrorResponseDTO(String message, Map<String, String> fieldErrors) {
}
