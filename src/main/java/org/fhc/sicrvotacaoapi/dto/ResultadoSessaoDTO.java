package org.fhc.sicrvotacaoapi.dto;

import org.fhc.sicrvotacaoapi.model.ResultadoVotacao;
import org.fhc.sicrvotacaoapi.model.SessaoVotacao;

public record ResultadoSessaoDTO(
        Long sessaoId,
        long totalSim,
        long totalNao,
        long totalVotos,
        ResultadoVotacao resultado,  // SIM ou NAO
        boolean aberta
) {
    public static ResultadoSessaoDTO fromSessao(SessaoVotacao sessao, long totalSim, long totalNao) {
        ResultadoVotacao resultado;
        if (totalSim > totalNao) resultado = ResultadoVotacao.SIM;
        else if (totalNao > totalSim) resultado = ResultadoVotacao.NAO;
        else resultado = ResultadoVotacao.EMPATE;

        return new ResultadoSessaoDTO(sessao.getId(), totalSim, totalNao, totalSim + totalNao, resultado, sessao.isAberta());
    }
}
