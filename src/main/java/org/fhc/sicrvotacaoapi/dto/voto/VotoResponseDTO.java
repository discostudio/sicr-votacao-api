package org.fhc.sicrvotacaoapi.dto.voto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.fhc.sicrvotacaoapi.model.Voto;
import org.fhc.sicrvotacaoapi.model.VotoValor;

@Schema(description = "DTO de resposta após registrar um voto")
public record VotoResponseDTO(@Schema(description = "ID da pauta", example = "1")
                              Long pautaId,
                              @Schema(description = "ID da sessão de votação", example = "1")
                              Long sessaoId,
                              @Schema(description = "CPF do associado", example = "11122233345")
                              String associadoCpf,
                              @Schema(description = "Valor do voto - SIM ou NAO", example = "SIM")
                              VotoValor valor
) {
    public static VotoResponseDTO fromEntity(Voto voto) {
        return new VotoResponseDTO(
                voto.getSessao().getPauta().getId(),
                voto.getSessao().getId(),
                voto.getAssociadoCpf(),
                voto.getValor()
        );
    }
}
