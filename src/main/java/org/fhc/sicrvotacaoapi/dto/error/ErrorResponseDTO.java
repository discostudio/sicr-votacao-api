package org.fhc.sicrvotacaoapi.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Schema(description = "DTO para retorno de mansagens de erro")
public record ErrorResponseDTO(@Schema(description = "Mensagem de erro", example = "Não foi possível criar a sessão")
                               String message,
                               @Schema(description = "Erros relacionados com campos da requisição", example = "pautaId: Não há sessão de votação aberta para a pauta com o ID 1")
                               Map<String, String> fieldErrors) {
}
