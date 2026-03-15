package org.fhc.sicrvotacaoapi.dto.resultado;

import io.swagger.v3.oas.annotations.media.Schema;
import org.fhc.sicrvotacaoapi.model.ResultadoVotacao;

import java.util.List;

@Schema(description = "DTO para retorno do resultado consolidado da votação de uma pauta, incluindo detalhes")
public record ResultadoVotacaoConsolidadoDTO(@Schema(description = "ID da pauta", example = "1")
                                             Long pautaId,
                                             @Schema(description = "Total de votos SIM na pauta", example = "1")
                                             long totalSim,
                                             @Schema(description = "Total de votos NAO na pauta", example = "1")
                                             long totalNao,
                                             @Schema(description = "Total de votos na pauta", example = "2")
                                             long totalVotos,
                                             @Schema(description = "Resultado da pauta (SIM, NAO ou EMPATE)", example = "EMPATE")
                                             ResultadoVotacao resultado,          // SIM ou NAO consolidado
                                             @Schema(description = "Indica se a pauta possui sessões abertas", example = "true")
                                             boolean possuiSessoesAbertas,
                                             @Schema(
                                                     description = "Detalhamento das sessões da pauta",
                                                     example = """
                                                        [
                                                          {
                                                            "sessaoId": 10,
                                                            "totalSim": 3,
                                                            "totalNao": 2,
                                                            "totalVotos": 5,
                                                            "resultado": "SIM",
                                                            "aberta": false
                                                          }
                                                        ]
                                                    """
                                             )
                                             List<ResultadoSessaoDTO> sessoes
) {}
