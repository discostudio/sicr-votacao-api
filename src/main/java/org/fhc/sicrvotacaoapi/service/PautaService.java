package org.fhc.sicrvotacaoapi.service;

import lombok.extern.slf4j.Slf4j;
import org.fhc.sicrvotacaoapi.dto.pauta.PautaRequestDTO;
import org.fhc.sicrvotacaoapi.dto.pauta.PautaResponseDTO;
import org.fhc.sicrvotacaoapi.model.Pauta;
import org.fhc.sicrvotacaoapi.repository.PautaRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PautaService {
    private final PautaRepository pautaRepository;

    public PautaService(PautaRepository pautaRepository) {
        this.pautaRepository = pautaRepository;
    }

    public PautaResponseDTO criarPauta(PautaRequestDTO dto) {
        Pauta pauta = new Pauta(dto.nome(), dto.descricao());

        pauta = pautaRepository.save(pauta);
        log.info("Pauta salva com id={}", pauta.getId());

        return PautaResponseDTO.fromEntity(pauta);
    }
}
