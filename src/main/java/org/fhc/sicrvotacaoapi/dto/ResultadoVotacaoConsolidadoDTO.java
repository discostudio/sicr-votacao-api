package org.fhc.sicrvotacaoapi.dto;

import java.util.List;

public record ResultadoVotacaoConsolidadoDTO(
        Long pautaId,
        long totalSim,
        long totalNao,
        long totalVotos,
        String resultado,          // SIM ou NAO consolidado
        List<ResultadoSessaoDTO> sessoes
) {}
