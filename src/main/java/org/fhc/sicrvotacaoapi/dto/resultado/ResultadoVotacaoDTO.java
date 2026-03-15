package org.fhc.sicrvotacaoapi.dto.resultado;

import io.swagger.v3.oas.annotations.media.Schema;
import org.fhc.sicrvotacaoapi.model.ResultadoVotacao;

@Schema(description = "DTO para retorno do resultado da votação de uma pauta")
public record ResultadoVotacaoDTO(@Schema(description = "ID da pauta", example = "1")
                                  Long pautaId,
                                  @Schema(description = "Resultado da pauta (SIM, NAO ou EMPATE)", example = "EMPATE")
                                  ResultadoVotacao resultado,  // SIM ou NAO
                                  @Schema(description = "Indica se a pauta possui sessões abertas", example = "true")
                                  boolean possuiSessoesAbertas
) {}
