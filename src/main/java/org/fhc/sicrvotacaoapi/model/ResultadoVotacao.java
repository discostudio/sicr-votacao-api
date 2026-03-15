package org.fhc.sicrvotacaoapi.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resultado da votação de uma pauta")
public enum ResultadoVotacao {
    @Schema(description = "A votação terminou com maioria de votos SIM")
    SIM,
    @Schema(description = "A votação terminou com maioria de votos NAO")
    NAO,
    @Schema(description = "A votação terminou com empate entre os votos SIM e NAO")
    EMPATE
}
