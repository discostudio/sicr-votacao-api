package org.fhc.sicrvotacaoapi.dto;

import org.fhc.sicrvotacaoapi.model.ResultadoVotacao;

import java.util.List;

public record ResultadoVotacaoConsolidadoDTO(
        Long pautaId,
        long totalSim,
        long totalNao,
        long totalVotos,
        ResultadoVotacao resultado,          // SIM ou NAO consolidado
        boolean possuiSessoesAbertas,
        List<ResultadoSessaoDTO> sessoes
) {}
