package org.fhc.sicrvotacaoapi.dto;

import jakarta.validation.constraints.NotNull;

public record VotoRequestDTO(
        @NotNull(message = "ID da pauta é obrigatório")
        Long pautaId,

        @NotNull(message = "ID do associado é obrigatório")
        Long associadoId,

        @NotNull(message = "Valor do voto é obrigatório")
        String valor  // depois vamos converter para Enum SIM/NAO
) {}
