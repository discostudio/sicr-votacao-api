package org.fhc.sicrvotacaoapi.dto.sessao;

import org.fhc.sicrvotacaoapi.model.SessaoVotacao;

import java.time.LocalDateTime;

public record SessaoResponseDTO(Long id,
                                Long pautaId,
                                LocalDateTime inicio,
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
