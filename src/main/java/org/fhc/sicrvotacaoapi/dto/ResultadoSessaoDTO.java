package org.fhc.sicrvotacaoapi.dto;

public record ResultadoSessaoDTO(
        Long sessaoId,
        long totalSim,
        long totalNao,
        long totalVotos,
        String resultado  // SIM ou NAO
) {
    public static ResultadoSessaoDTO fromCounts(Long sessaoId, long totalSim, long totalNao) {
        String resultado;
        if (totalSim > totalNao) resultado = "SIM";
        else if (totalNao > totalSim) resultado = "NAO";
        else resultado = "EMPATE";
        return new ResultadoSessaoDTO(sessaoId, totalSim, totalNao, totalSim + totalNao, resultado);
    }
}
