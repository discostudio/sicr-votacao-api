package org.fhc.sicrvotacaoapi.dto.resultado;

import org.fhc.sicrvotacaoapi.model.ResultadoVotacao;

public record ResultadoVotacaoDTO(
        Long pautaId,
        ResultadoVotacao resultado,  // SIM ou NAO
        boolean possuiSessoesAbertas
) {}
