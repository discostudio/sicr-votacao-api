package org.fhc.sicrvotacaoapi.dto.pauta;

import io.swagger.v3.oas.annotations.media.Schema;
import org.fhc.sicrvotacaoapi.model.Pauta;

@Schema(description = "DTO de resposta após criar uma pauta")
public record PautaResponseDTO(@Schema(description = "ID da pauta", example = "1")
                               Long id,
                               @Schema(description = "Nome da pauta", example = "Orçamento 2026")
                               String nome,
                               @Schema(description = "Descrição da pauta", example = "Esta pauta vai decidir sobre o orçamento de 2026")
                               String descricao) {

    public static PautaResponseDTO fromEntity(Pauta pauta) {
        return new PautaResponseDTO(
                pauta.getId(),
                pauta.getNome(),
                pauta.getDescricao()
        );
    }
}
