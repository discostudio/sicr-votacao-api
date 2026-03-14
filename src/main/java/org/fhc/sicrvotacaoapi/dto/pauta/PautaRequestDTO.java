package org.fhc.sicrvotacaoapi.dto.pauta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PautaRequestDTO(@NotNull(message = "O campo nome é obrigatório")
                              @NotBlank(message = "O nome da pauta é obrigatório")
                              String nome,
                              String descricao) {
}
