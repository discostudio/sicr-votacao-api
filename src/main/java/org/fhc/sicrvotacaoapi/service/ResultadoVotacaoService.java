package org.fhc.sicrvotacaoapi.service;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.fhc.sicrvotacaoapi.dto.resultado.ResultadoSessaoDTO;
import org.fhc.sicrvotacaoapi.dto.resultado.ResultadoVotacaoDTO;
import org.fhc.sicrvotacaoapi.dto.resultado.ResultadoVotacaoDetalhadoDTO;
import org.fhc.sicrvotacaoapi.exception.BusinessException;
import org.fhc.sicrvotacaoapi.model.ResultadoVotacao;
import org.fhc.sicrvotacaoapi.model.SessaoVotacao;
import org.fhc.sicrvotacaoapi.model.VotoValor;
import org.fhc.sicrvotacaoapi.repository.PautaRepository;
import org.fhc.sicrvotacaoapi.repository.SessaoVotacaoRepository;
import org.fhc.sicrvotacaoapi.repository.VotoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ResultadoVotacaoService {

    private final SessaoVotacaoRepository sessaoRepository;
    private final VotoRepository votoRepository;
    private final PautaRepository pautaRepository;
    //private final SessaoVotacaoService sessaoService;

    public ResultadoVotacaoService(SessaoVotacaoRepository sessaoRepository,
                                   VotoRepository votoRepository,
                                   PautaRepository pautaRepository,
                                   SessaoVotacaoService sessaoService) {
        this.sessaoRepository = sessaoRepository;
        this.votoRepository = votoRepository;
        this.pautaRepository = pautaRepository;
        //this.sessaoService = sessaoService;
    }

    public ResultadoVotacaoDetalhadoDTO obterResultadoDetalhado(Long pautaId, Pageable pageable) {

        // PERFORMANCE: busca pauta e sessões com paginação
        Page<SessaoVotacao> sessoesPaginadas = buscarSessoesDaPautaComPaginacao(pautaId, pageable);

        // 1. Busca totais gerais da pauta
        TotaisVotos totaisGerais = buscarTotaisGerais(pautaId);
        validaVotosEmSessoes(totaisGerais.totalSim(), totaisGerais.totalNao(), pautaId);

        // 2. Performance: Busca votos das sessões da página de uma vez só
        List<Long> idsSessoes = sessoesPaginadas.getContent().stream().map(SessaoVotacao::getId).toList();
        Map<Long, TotaisVotos> mapaVotosPorSessao = buscarTotaisAgrupadosPorSessao(idsSessoes);

        // 3. Mapeia para o DTO sem novas queries no banco
        List<ResultadoSessaoDTO> resultadosPorSessao = sessoesPaginadas.stream()
                .map(sessao -> {
                    TotaisVotos t = mapaVotosPorSessao.getOrDefault(sessao.getId(), new TotaisVotos(0L, 0L));
                    return ResultadoSessaoDTO.fromSessao(sessao, t.totalSim(), t.totalNao());
                })
                .toList();

        log.info("ResultadoVotacaoService: retornando resultado detalhado da pauta ID {}", pautaId);

        return new ResultadoVotacaoDetalhadoDTO(
                pautaId,
                totaisGerais.totalSim(),
                totaisGerais.totalNao(),
                totaisGerais.soma(),
                calcularResultado(totaisGerais.totalSim(), totaisGerais.totalNao()),
                possuiSessoesAbertas(pautaId),
                resultadosPorSessao,
                pageable.getPageNumber(),  // número da página atual
                pageable.getPageSize()     // tamanho da página
        );
    }

    public ResultadoVotacaoDTO obterResultado(Long pautaId) {

        // Valida se existe
        buscarSessoesDaPauta(pautaId);

        TotaisVotos totais = buscarTotaisGerais(pautaId);
        validaVotosEmSessoes(totais.totalSim(), totais.totalNao(), pautaId);

        log.info("ResultadoVotacaoService: retornando resultado da pauta {}", pautaId);

        return new ResultadoVotacaoDTO(
                pautaId,
                calcularResultado(totais.totalSim(), totais.totalNao()),
                possuiSessoesAbertas(pautaId)
        );
    }

    private Page<SessaoVotacao> buscarSessoesDaPautaComPaginacao(Long pautaId, Pageable pageable) {

        // Busca a pauta
        pautaRepository.findById(pautaId)
                .orElseThrow(() -> new BusinessException(
                        "Pauta não encontrada.",
                        HttpStatus.NOT_FOUND,
                        Map.of("pautaId", "Não há pauta com o ID " + pautaId)
                ));

        Page<SessaoVotacao> sessoesPaginadas = sessaoRepository.findAllByPautaIdOrderByFimAsc(pautaId, pageable);

        if (sessoesPaginadas.isEmpty()) {
            throw new BusinessException(
                    "Sessão não encontrada.",
                    HttpStatus.BAD_REQUEST,
                    Map.of("pautaId", "Não há sessão para a pauta " + pautaId)
            );
        }

        return sessoesPaginadas;
    }

    private List<SessaoVotacao> buscarSessoesDaPauta(Long pautaId) {

        // Busca a pauta
        pautaRepository.findById(pautaId)
                .orElseThrow(() -> new BusinessException(
                        "Pauta não encontrada.",
                        HttpStatus.NOT_FOUND,
                        Map.of("pautaId", "Não há pauta com o ID " + pautaId)
                ));

        List<SessaoVotacao> sessoes = sessaoRepository.findAllByPautaIdOrderByFimAsc(pautaId);

        if (sessoes.isEmpty()) {
            throw new BusinessException(
                    "Sessão não encontrada.",
                    HttpStatus.BAD_REQUEST,
                    Map.of("pautaId", "Não há sessão para a pauta " + pautaId)
            );
        }

        return sessoes;
    }

    private void validaVotosEmSessoes(Long totalSim, Long totalNao, Long pautaId) {
        if (totalSim + totalNao == 0) {
            throw new BusinessException(
                    "Resultado não encontrado.",
                    HttpStatus.NOT_FOUND,
                    Map.of("pautaId", "Não há votos registrados para a pauta " + pautaId)
            );
        }
    }

    private boolean possuiSessoesAbertas(Long pautaId) {
        return sessaoRepository.existsByPautaIdAndFimAfter(pautaId, LocalDateTime.now());
    }

    private ResultadoVotacao calcularResultado(long totalSim, long totalNao) {
        if (totalSim > totalNao) return ResultadoVotacao.SIM;
        if (totalNao > totalSim) return ResultadoVotacao.NAO;
        return ResultadoVotacao.EMPATE;
    }

    // Record auxiliar pata total de votos
    private record TotaisVotos(long totalSim, long totalNao) {
        long soma() { return totalSim + totalNao; }
    }

    private TotaisVotos buscarTotaisGerais(Long pautaId) {
        List<Object[]> resultados = votoRepository.countVotosGroupByValor(pautaId);
        long sim = 0;
        long nao = 0;
        for (Object[] linha : resultados) {
            if (VotoValor.SIM.equals(linha[0])) sim = (long) linha[1];
            else if (VotoValor.NAO.equals(linha[0])) nao = (long) linha[1];
        }
        return new TotaisVotos(sim, nao);
    }

    private Map<Long, TotaisVotos> buscarTotaisAgrupadosPorSessao(List<Long> idsSessoes) {
        if (idsSessoes.isEmpty()) return Collections.emptyMap();

        return votoRepository.countVotosAgrupadosPorSessoes(idsSessoes).stream()
                .collect(Collectors.groupingBy(
                        linha -> (Long) linha[0], // Agrupa pelo SessaoID
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                lista -> {
                                    long sim = lista.stream()
                                            .filter(l -> VotoValor.SIM.equals(l[1]))
                                            .mapToLong(l -> (long) l[2]).findFirst().orElse(0L);
                                    long nao = lista.stream()
                                            .filter(l -> VotoValor.NAO.equals(l[1]))
                                            .mapToLong(l -> (long) l[2]).findFirst().orElse(0L);
                                    return new TotaisVotos(sim, nao);
                                }
                        )
                ));
    }
}
