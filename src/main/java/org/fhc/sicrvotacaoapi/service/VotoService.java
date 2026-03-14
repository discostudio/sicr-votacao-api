package org.fhc.sicrvotacaoapi.service;

import jakarta.transaction.Transactional;
import org.fhc.sicrvotacaoapi.dto.VotoRequestDTO;
import org.fhc.sicrvotacaoapi.dto.VotoResponseDTO;
import org.fhc.sicrvotacaoapi.exception.AssociadoJaVotouException;
import org.fhc.sicrvotacaoapi.exception.SessaoFechadaException;
import org.fhc.sicrvotacaoapi.exception.SessaoNaoEncontradaException;
import org.fhc.sicrvotacaoapi.exception.VotoInvalidoException;
import org.fhc.sicrvotacaoapi.model.SessaoVotacao;
import org.fhc.sicrvotacaoapi.model.Voto;
import org.fhc.sicrvotacaoapi.model.VotoValor;
import org.fhc.sicrvotacaoapi.repository.SessaoVotacaoRepository;
import org.fhc.sicrvotacaoapi.repository.VotoRepository;
import org.springframework.stereotype.Service;

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
        SessaoVotacao sessao = sessaoRepository.findById(votoRequest.sessaoId())
                .orElseThrow(() -> new SessaoNaoEncontradaException(votoRequest.sessaoId()));

        if (!sessao.isAberta()) {
            throw new SessaoFechadaException(sessao.getId());
        }

        if (votoRepository.existsBySessaoIdAndAssociadoId(sessao.getId(), votoRequest.associadoId())) {
            throw new AssociadoJaVotouException(sessao.getId(), votoRequest.associadoId());
        }

        VotoValor valor;
        try {
            valor = VotoValor.valueOf(votoRequest.valor().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new VotoInvalidoException(votoRequest.valor());
        }

        Voto voto = new Voto(sessao, votoRequest.associadoId(), valor);
        voto = votoRepository.save(voto);

        return VotoResponseDTO.fromEntity(voto);
    }
}
