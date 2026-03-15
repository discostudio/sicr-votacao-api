package org.fhc.sicrvotacaoapi.dto.voto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO para o registro de um voto em uma pauta")
public record VotoRequestDTO(@NotNull(message = "ID da pauta é obrigatório")
                             @Min(value = 1, message = "Id da pauta deve ser maior que zero")
                             @Schema(description = "ID da pauta", example = "1")
                             Long pautaId,
                             @Schema(description = "ID do associado", example = "1")
                             @Min(value = 1, message = "Id do associado deve ser maior que zero")
                             @NotNull(message = "ID do associado é obrigatório")
                             Long associadoId,
                             @Schema(description = "Valor do voto - SIM ou NAO", example = "SIM")
                             @NotNull(message = "Valor do voto é obrigatório")
                             String valor  // depois vamos converter para Enum SIM/NAO
) {}
