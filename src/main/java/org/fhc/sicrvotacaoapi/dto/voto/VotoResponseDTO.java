package org.fhc.sicrvotacaoapi.dto.voto;

import org.fhc.sicrvotacaoapi.model.Voto;

public record VotoResponseDTO(
        //Long id,
        Long pautaId,
        Long sessaoId,
        Long associadoId,
        String valor
) {
    public static VotoResponseDTO fromEntity(Voto voto) {
        return new VotoResponseDTO(
                //voto.getId(),
                voto.getSessao().getPauta().getId(),
                voto.getSessao().getId(),
                voto.getAssociadoId(),
                voto.getValor().name()
        );
    }
}
