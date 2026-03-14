package org.fhc.sicrvotacaoapi.service;

import org.fhc.sicrvotacaoapi.dto.ResultadoSessaoDTO;
import org.fhc.sicrvotacaoapi.dto.ResultadoVotacaoConsolidadoDTO;
import org.fhc.sicrvotacaoapi.dto.ResultadoVotacaoDTO;
import org.fhc.sicrvotacaoapi.exception.PautaNaoEncontradaException;
import org.fhc.sicrvotacaoapi.model.SessaoVotacao;
import org.fhc.sicrvotacaoapi.model.VotoValor;
import org.fhc.sicrvotacaoapi.repository.SessaoVotacaoRepository;
import org.fhc.sicrvotacaoapi.repository.VotoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResultadoVotacaoService {

    private final SessaoVotacaoRepository sessaoRepository;
    private final VotoRepository votoRepository;

    public ResultadoVotacaoService(SessaoVotacaoRepository sessaoRepository,
                                   VotoRepository votoRepository) {
        this.sessaoRepository = sessaoRepository;
        this.votoRepository = votoRepository;
    }

    public ResultadoVotacaoConsolidadoDTO obterResultadoConsolidado(Long pautaId) {
        List<SessaoVotacao> sessoes = buscarSessoesDaPauta(pautaId);

        List<ResultadoSessaoDTO> resultadosPorSessao = sessoes.stream()
                .map(this::calcularResultadoSessao)
                .toList();

        long totalSim = resultadosPorSessao.stream().mapToLong(ResultadoSessaoDTO::totalSim).sum();
        long totalNao = resultadosPorSessao.stream().mapToLong(ResultadoSessaoDTO::totalNao).sum();

        String resultado = calcularResultado(totalSim, totalNao);

        return new ResultadoVotacaoConsolidadoDTO(
                pautaId,
                totalSim,
                totalNao,
                totalSim + totalNao,
                resultado,
                resultadosPorSessao
        );
    }

    public ResultadoVotacaoDTO obterResultado(Long pautaId) {
        List<SessaoVotacao> sessoes = buscarSessoesDaPauta(pautaId);

        long totalSim = 0;
        long totalNao = 0;

        for (SessaoVotacao sessao : sessoes) {
            totalSim += votoRepository.countBySessaoIdAndValor(sessao.getId(), VotoValor.SIM);
            totalNao += votoRepository.countBySessaoIdAndValor(sessao.getId(), VotoValor.NAO);
        }

        String resultado = calcularResultado(totalSim, totalNao);

        return new ResultadoVotacaoDTO(pautaId, resultado);

    }

    private List<SessaoVotacao> buscarSessoesDaPauta(Long pautaId) {
        List<SessaoVotacao> sessoes = sessaoRepository.findAllByPautaIdOrderByFimAsc(pautaId);

        if (sessoes.isEmpty()) {
            throw new PautaNaoEncontradaException(pautaId);
        }

        return sessoes;
    }

    private ResultadoSessaoDTO calcularResultadoSessao(SessaoVotacao sessao) {

        long totalSim = votoRepository.countBySessaoIdAndValor(sessao.getId(), VotoValor.SIM);
        long totalNao = votoRepository.countBySessaoIdAndValor(sessao.getId(), VotoValor.NAO);

        return ResultadoSessaoDTO.fromCounts(sessao.getId(), totalSim, totalNao);
    }

    private String calcularResultado(long totalSim, long totalNao) {

        if (totalSim > totalNao) {
            return "SIM";
        }

        if (totalNao > totalSim) {
            return "NAO";
        }

        return "EMPATE";
    }
}
