package org.fhc.sicrvotacaoapi.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.fhc.sicrvotacaoapi.dto.SessaoRequestDTO;
import org.fhc.sicrvotacaoapi.dto.SessaoResponseDTO;
import org.fhc.sicrvotacaoapi.exception.PautaNaoEncontradaException;
import org.fhc.sicrvotacaoapi.exception.SessaoJaAbertaException;
import org.fhc.sicrvotacaoapi.model.Pauta;
import org.fhc.sicrvotacaoapi.model.SessaoVotacao;
import org.fhc.sicrvotacaoapi.repository.PautaRepository;
import org.fhc.sicrvotacaoapi.repository.SessaoVotacaoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class SessaoVotacaoService {

    private final SessaoVotacaoRepository sessaoRepository;
    private final PautaRepository pautaRepository;

    public SessaoVotacaoService(SessaoVotacaoRepository sessaoRepository,
                                PautaRepository pautaRepository) {
        this.sessaoRepository = sessaoRepository;
        this.pautaRepository = pautaRepository;
    }

    @Transactional
    public SessaoResponseDTO abrirSessao(SessaoRequestDTO dto) {

        // Busca a pauta relacionada com a sessão a ser inserida
        Pauta pauta = pautaRepository.findById(dto.pautaId())
                .orElseThrow(() -> new PautaNaoEncontradaException(dto.pautaId()));

        //if (sessaoRepository.existsByPautaId(pauta.getId())) {
        //    throw new SessaoJaAbertaException(pauta.getId());
        //}
        // verifica se existe sessão aberta para a pauta
        if (sessaoRepository.existsByPautaIdAndFimAfter(pauta.getId(), LocalDateTime.now())) {
            throw new SessaoJaAbertaException(pauta.getId());
        }

        // define duração padrão de 1 minuto
        int duracao = dto.duracaoEmMinutos() != null ? dto.duracaoEmMinutos() : 1;

        // define inicio e fim da sessão
        LocalDateTime inicio = LocalDateTime.now();
        LocalDateTime fim = inicio.plusMinutes(duracao);

        SessaoVotacao sessao = new SessaoVotacao(pauta, inicio, fim);
        sessao = sessaoRepository.save(sessao);

        log.info("Sessão salva com id={}", sessao.getId());

        return SessaoResponseDTO.fromEntity(sessao);
    }
}
