package org.fhc.sicrvotacaoapi.dto;

import org.fhc.sicrvotacaoapi.model.ResultadoVotacao;

public record ResultadoVotacaoDTO(
        Long pautaId,
        ResultadoVotacao resultado,  // SIM ou NAO
        boolean possuiSessoesAbertas
) {}
