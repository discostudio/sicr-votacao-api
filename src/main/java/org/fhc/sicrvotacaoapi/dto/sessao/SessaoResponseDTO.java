package org.fhc.sicrvotacaoapi.dto.sessao;

import io.swagger.v3.oas.annotations.media.Schema;
import org.fhc.sicrvotacaoapi.model.SessaoVotacao;

import java.time.LocalDateTime;

@Schema(description = "DTO de resposta após criar uma sessão de votação")
public record SessaoResponseDTO(@Schema(description = "ID da sessão de votação", example = "1")
                                Long id,
                                @Schema(description = "ID da pauta", example = "1")
                                Long pautaId,
                                @Schema(description = "Data e hora de início da sessão de votação", example = "2026-03-14T20:57:48.2363188")
                                LocalDateTime inicio,
                                @Schema(description = "Data e hora de fim da sessão de votação", example = "2026-03-14T20:57:48.2363188")
                                LocalDateTime fim) {

    public static SessaoResponseDTO fromEntity(SessaoVotacao sessao) {
        return new SessaoResponseDTO(
                sessao.getId(),
                sessao.getPauta().getId(),
                sessao.getInicio(),
                sessao.getFim()
        );
    }
}
