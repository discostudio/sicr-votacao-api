package org.fhc.sicrvotacaoapi.dto.resultado;

import io.swagger.v3.oas.annotations.media.Schema;
import org.fhc.sicrvotacaoapi.model.ResultadoVotacao;
import org.fhc.sicrvotacaoapi.model.SessaoVotacao;

@Schema(description = "DTO para retorno do resultado da votação de uma sessão")
public record ResultadoSessaoDTO(@Schema(description = "ID da sessão de votação", example = "1")
                                 Long sessaoId,
                                 @Schema(description = "Total de votos SIM na sessão de votação", example = "1")
                                 long totalSim,
                                 @Schema(description = "Total de votos NAO na sessão de votação", example = "1")
                                 long totalNao,
                                 @Schema(description = "Total de votos na sessão de votação", example = "2")
                                 long totalVotos,
                                 @Schema(description = "Resultado da sessão de votação (SIM, NAO ou EMPATE)", example = "EMPATE")
                                 ResultadoVotacao resultado,  // SIM ou NAO
                                 @Schema(description = "Indica se a sessão de votação está aberta", example = "true")
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
