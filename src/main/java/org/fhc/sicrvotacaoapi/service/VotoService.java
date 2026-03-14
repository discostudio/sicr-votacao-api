package org.fhc.sicrvotacaoapi.service;

import jakarta.transaction.Transactional;
import org.fhc.sicrvotacaoapi.dto.VotoRequestDTO;
import org.fhc.sicrvotacaoapi.dto.VotoResponseDTO;
import org.fhc.sicrvotacaoapi.exception.*;
import org.fhc.sicrvotacaoapi.model.SessaoVotacao;
import org.fhc.sicrvotacaoapi.model.Voto;
import org.fhc.sicrvotacaoapi.model.VotoValor;
import org.fhc.sicrvotacaoapi.repository.SessaoVotacaoRepository;
import org.fhc.sicrvotacaoapi.repository.VotoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VotoService {

    private final VotoRepository votoRepository;
    private final SessaoVotacaoRepository sessaoRepository;

    public VotoService(VotoRepository votoRepository, SessaoVotacaoRepository sessaoRepository) {
        this.votoRepository = votoRepository;
        this.sessaoRepository = sessaoRepository;
    }

    @Transactional
    public VotoResponseDTO registrarVoto(VotoRequestDTO votoRequest) {

        // buscar sessão aberta da pauta
        SessaoVotacao sessaoAberta = sessaoRepository
                .findByPautaIdAndFimAfter(votoRequest.pautaId(), LocalDateTime.now())
                .orElseThrow(() -> new PautaSemSessoesAbertasException(votoRequest.pautaId()));

        // Validar se associado já votou na pauta (na sessão atual ou em sessões anteriores)
        if (votoRepository.existsBySessaoPautaIdAndAssociadoId(votoRequest.pautaId(), votoRequest.associadoId())) {
            throw new AssociadoJaVotouException(votoRequest.pautaId(), votoRequest.associadoId());
        }

        VotoValor valor;
        try {
            valor = VotoValor.valueOf(votoRequest.valor().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new VotoInvalidoException(votoRequest.valor());
        }

        Voto voto = new Voto(sessaoAberta, votoRequest.associadoId(), valor);
        voto = votoRepository.save(voto);

        return VotoResponseDTO.fromEntity(voto);
    }
}
