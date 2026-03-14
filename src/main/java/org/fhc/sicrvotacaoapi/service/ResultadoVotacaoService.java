package org.fhc.sicrvotacaoapi.service;

import org.fhc.sicrvotacaoapi.dto.ResultadoSessaoDTO;
import org.fhc.sicrvotacaoapi.dto.ResultadoVotacaoConsolidadoDTO;
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
        List<SessaoVotacao> sessoes = sessaoRepository.findAllByPautaIdOrderByFimAsc(pautaId);

        if (sessoes.isEmpty()) {
            throw new PautaNaoEncontradaException(pautaId);
        }

        List<ResultadoSessaoDTO> resultadosPorSessao = sessoes.stream().map(sessao -> {
            long totalSim = votoRepository.countBySessaoIdAndValor(sessao.getId(), VotoValor.SIM);
            long totalNao = votoRepository.countBySessaoIdAndValor(sessao.getId(), VotoValor.NAO);
            return ResultadoSessaoDTO.fromCounts(sessao.getId(), totalSim, totalNao);
        }).collect(Collectors.toList());

        long totalSim = resultadosPorSessao.stream().mapToLong(ResultadoSessaoDTO::totalSim).sum();
        long totalNao = resultadosPorSessao.stream().mapToLong(ResultadoSessaoDTO::totalNao).sum();

        String resultadoConsolidado;
        if (totalSim > totalNao) resultadoConsolidado = "SIM";
        else if (totalNao > totalSim) resultadoConsolidado = "NAO";
        else resultadoConsolidado = "EMPATE";

        return new ResultadoVotacaoConsolidadoDTO(
                pautaId,
                totalSim,
                totalNao,
                totalSim + totalNao,
                resultadoConsolidado,
                resultadosPorSessao
        );
    }
}
