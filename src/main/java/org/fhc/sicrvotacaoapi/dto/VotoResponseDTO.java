package org.fhc.sicrvotacaoapi.dto;

import org.fhc.sicrvotacaoapi.model.Voto;

public record VotoResponseDTO(
        Long id,
        Long sessaoId,
        Long associadoId,
        String valor
) {
    public static VotoResponseDTO fromEntity(Voto voto) {
        return new VotoResponseDTO(
                voto.getId(),
                voto.getSessao().getId(),
                voto.getAssociadoId(),
                voto.getValor().name()
        );
    }
}
