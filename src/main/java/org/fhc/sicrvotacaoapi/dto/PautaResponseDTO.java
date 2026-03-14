package org.fhc.sicrvotacaoapi.dto;

import lombok.Getter;
import org.fhc.sicrvotacaoapi.model.Pauta;

public record PautaResponseDTO(Long id, String nome, String descricao) {

    public static PautaResponseDTO fromEntity(Pauta pauta) {
        return new PautaResponseDTO(
                pauta.getId(),
                pauta.getNome(),
                pauta.getDescricao()
        );
    }
}
