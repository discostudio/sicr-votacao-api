package org.fhc.sicrvotacaoapi.dto.sessao;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO para a criação de uma sessão de votação")
public record SessaoRequestDTO(@NotNull(message = "Id da pauta é obrigatório")
                               @Min(value = 1, message = "Id da pauta deve ser maior que zero")
                               @Schema(description = "ID da pauta", example = "1")
                               Long pautaId,
                               @Min(value = 1, message = "A duração mínima da sessão é de 1 minuto")
                               @Schema(description = "Duração da pauta (em minutos)", example = "60")
                               Integer duracaoEmMinutos) {
}
