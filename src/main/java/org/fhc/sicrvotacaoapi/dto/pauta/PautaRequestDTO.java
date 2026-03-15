package org.fhc.sicrvotacaoapi.dto.pauta;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO para a criação de uma pauta")
public record PautaRequestDTO(@NotNull(message = "O campo nome é obrigatório")
                              @NotBlank(message = "O nome da pauta é obrigatório")
                              @Schema(description = "Nome da pauta", example = "Orçamento 2026")
                              String nome,
                              @Schema(description = "Descrição da pauta", example = "Esta pauta vai decidir sobre o orçamento de 2026")
                              String descricao) {
}
