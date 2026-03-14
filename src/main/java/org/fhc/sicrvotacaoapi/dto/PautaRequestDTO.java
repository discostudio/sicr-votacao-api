package org.fhc.sicrvotacaoapi.dto;

import jakarta.validation.constraints.NotBlank;

public record PautaRequestDTO(@NotBlank
                              String nome,
                              String descricao) {
}
