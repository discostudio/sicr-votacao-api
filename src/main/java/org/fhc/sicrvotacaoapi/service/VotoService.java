package org.fhc.sicrvotacaoapi.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
public class VotoService {

    private final VotoRepository votoRepository;
    private final SessaoVotacaoRepository sessaoRepository;
    private final PautaRepository  pautaRepository;
    private final CPFValidacaoService cpfValidacaoService;

    public VotoService(VotoRepository votoRepository, SessaoVotacaoRepository sessaoRepository,
                       PautaRepository pautaRepository, CPFValidacaoService cpfValidacaoService) {
        this.votoRepository = votoRepository;
        this.sessaoRepository = sessaoRepository;
        this.pautaRepository = pautaRepository;
        this.cpfValidacaoService = cpfValidacaoService;
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

        // Validar pelo CPF do associado se ele pode votar
        boolean podeVotar = cpfValidacaoService.podeVotar(votoRequest.associadoCPF());
        if (!podeVotar) {
            throw new BusinessException(
                    "Não foi possível registrar o voto.",
                    HttpStatus.BAD_REQUEST,
                    Map.of("associadoCPF", "CPF " + votoRequest.associadoCPF() + " não está apto para votar")
            );
        }

        // Validar se associado já votou na pauta (na sessão atual ou em sessões anteriores)
        if (votoRepository.existsBySessaoPautaIdAndAssociadoCpf(pauta.getId(), votoRequest.associadoCPF())) {
            throw new BusinessException(
                    "Não foi possível registrar o voto.",
                    HttpStatus.CONFLICT,
                    Map.of("associadoId", "Já há um voto na pauta para o associado com o CPF " + votoRequest.associadoCPF())
            );
        }

        Voto voto = new Voto(sessaoAberta, pauta, votoRequest.associadoCPF(), votoRequest.valor());
        voto = votoRepository.save(voto);

        log.info("VotoService: voto salvo para a pauta {} e associado {}.", voto.getPauta().getId(), voto.getAssociadoCpf());

        return VotoResponseDTO.fromEntity(voto);
    }
}
