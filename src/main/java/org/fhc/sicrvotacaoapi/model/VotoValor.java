package org.fhc.sicrvotacaoapi.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Valor do voto de um associado")
public enum VotoValor {
    @Schema(description = "Voto para SIM - aprovar a pauta")
    SIM,
    @Schema(description = "Voto para NAO - reprovar a pauta")
    NAO
}
