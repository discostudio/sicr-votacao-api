package org.fhc.sicrvotacaoapi.dto.sessao;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SessaoRequestDTO(@NotNull(message = "Id da pauta é obrigatório")
                               @Min(value = 1, message = "Id da pauta deve ser maior que zero")
                               Long pautaId,
                               @Min(value = 1, message = "A duração mínima da sessão é de 1 minuto")
                               Integer duracaoEmMinutos) {
}
