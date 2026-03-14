package org.fhc.sicrvotacaoapi.service;

import jakarta.transaction.Transactional;
import org.fhc.sicrvotacaoapi.dto.voto.VotoRequestDTO;
import org.fhc.sicrvotacaoapi.dto.voto.VotoResponseDTO;
import org.fhc.sicrvotacaoapi.exception.*;
import org.fhc.sicrvotacaoapi.model.Pauta;
import org.fhc.sicrvotacaoapi.model.SessaoVotacao;
import org.fhc.sicrvotacaoapi.model.Voto;
import org.fhc.sicrvotacaoapi.model.VotoValor;
import org.fhc.sicrvotacaoapi.repository.PautaRepository;
import org.fhc.sicrvotacaoapi.repository.SessaoVotacaoRepository;
import org.fhc.sicrvotacaoapi.repository.VotoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class VotoService {

    private final VotoRepository votoRepository;
    private final SessaoVotacaoRepository sessaoRepository;
    private final PautaRepository  pautaRepository;

    public VotoService(VotoRepository votoRepository, SessaoVotacaoRepository sessaoRepository, PautaRepository pautaRepository) {
        this.votoRepository = votoRepository;
        this.sessaoRepository = sessaoRepository;
        this.pautaRepository = pautaRepository;
    }

    @Transactional
    public VotoResponseDTO registrarVoto(VotoRequestDTO votoRequest) {

        // Busca a pauta
        Pauta pauta = pautaRepository.findById(votoRequest.pautaId())
                .orElseThrow(() -> new BusinessException(
                        "Não foi possível registrar o voto.",
                        HttpStatus.NOT_FOUND,
                        Map.of("pautaId", "Pauta não encontrada com o ID " + votoRequest.pautaId())
                ));

        // buscar sessão aberta da pauta
        SessaoVotacao sessaoAberta = sessaoRepository
                .findByPautaIdAndFimAfter(pauta.getId(), LocalDateTime.now())
                .orElseThrow(() -> new BusinessException(
                        "Não foi possível registrar o voto.",
                        HttpStatus.NOT_FOUND,
                        Map.of("pautaId", "Não há sessão de votação aberta para a pauta com o ID " + pauta.getId())
                ));

        // Validar se associado já votou na pauta (na sessão atual ou em sessões anteriores)
        if (votoRepository.existsBySessaoPautaIdAndAssociadoId(pauta.getId(), votoRequest.associadoId())) {
            throw new BusinessException(
                    "Não foi possível registrar o voto.",
                    HttpStatus.CONFLICT,
                    Map.of("associadoId", "Já há um voto na pauta para o associado com o ID " + votoRequest.associadoId())
            );
        }

        // Valida parâmetro do voto - SIM ou NAO
        VotoValor valor;
        try {
            valor = VotoValor.valueOf(votoRequest.valor().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(
                    "Não foi possível registrar o voto.",
                    HttpStatus.BAD_REQUEST,
                    Map.of("valor", "Valor do voto inválido: '" + votoRequest.valor() + "'. Use 'SIM' ou 'NAO'.")
            );
        }

        Voto voto = new Voto(sessaoAberta, votoRequest.associadoId(), valor);
        voto = votoRepository.save(voto);

        return VotoResponseDTO.fromEntity(voto);
    }
}
