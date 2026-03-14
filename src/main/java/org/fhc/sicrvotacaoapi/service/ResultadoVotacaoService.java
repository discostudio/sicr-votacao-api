package org.fhc.sicrvotacaoapi.service;

import org.fhc.sicrvotacaoapi.dto.resultado.ResultadoSessaoDTO;
import org.fhc.sicrvotacaoapi.dto.resultado.ResultadoVotacaoConsolidadoDTO;
import org.fhc.sicrvotacaoapi.dto.resultado.ResultadoVotacaoDTO;
import org.fhc.sicrvotacaoapi.exception.BusinessException;
import org.fhc.sicrvotacaoapi.model.ResultadoVotacao;
import org.fhc.sicrvotacaoapi.model.SessaoVotacao;
import org.fhc.sicrvotacaoapi.model.VotoValor;
import org.fhc.sicrvotacaoapi.repository.PautaRepository;
import org.fhc.sicrvotacaoapi.repository.SessaoVotacaoRepository;
import org.fhc.sicrvotacaoapi.repository.VotoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ResultadoVotacaoService {

    private final SessaoVotacaoRepository sessaoRepository;
    private final VotoRepository votoRepository;
    private final PautaRepository pautaRepository;

    public ResultadoVotacaoService(SessaoVotacaoRepository sessaoRepository,
                                   VotoRepository votoRepository,
                                   PautaRepository pautaRepository) {
        this.sessaoRepository = sessaoRepository;
        this.votoRepository = votoRepository;
        this.pautaRepository = pautaRepository;
    }

    public ResultadoVotacaoConsolidadoDTO obterResultadoConsolidado(Long pautaId) {
        List<SessaoVotacao> sessoes = buscarSessoesDaPauta(pautaId);

        List<ResultadoSessaoDTO> resultadosPorSessao = sessoes.stream()
                .map(sessao -> {
                    long totalSim = votoRepository.countBySessaoIdAndValor(sessao.getId(), VotoValor.SIM);
                    long totalNao = votoRepository.countBySessaoIdAndValor(sessao.getId(), VotoValor.NAO);
                    return ResultadoSessaoDTO.fromSessao(sessao, totalSim, totalNao);
                })
                .toList();

        long totalSim = resultadosPorSessao.stream().mapToLong(ResultadoSessaoDTO::totalSim).sum();
        long totalNao = resultadosPorSessao.stream().mapToLong(ResultadoSessaoDTO::totalNao).sum();

        return new ResultadoVotacaoConsolidadoDTO(
                pautaId,
                totalSim,
                totalNao,
                totalSim + totalNao,
                calcularResultado(totalSim, totalNao),
                possuiSessoesAbertas(sessoes),
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

        return new ResultadoVotacaoDTO(
                pautaId,
                calcularResultado(totalSim, totalNao),
                possuiSessoesAbertas(sessoes));

    }

    private List<SessaoVotacao> buscarSessoesDaPauta(Long pautaId) {

        // Busca a pauta
        pautaRepository.findById(pautaId)
                .orElseThrow(() -> new BusinessException(
                                            "Resultado não encontrado.",
                                            HttpStatus.NOT_FOUND,
                                            Map.of("pautaId", "Não há pauta com o ID " + pautaId)
                ));

        List<SessaoVotacao> sessoes = sessaoRepository.findAllByPautaIdOrderByFimAsc(pautaId);

        if (sessoes.isEmpty()) {
            //throw new PautaSemSessoesException(pautaId);
            throw new BusinessException(
                            "Resultado não encontrado.",
                            HttpStatus.BAD_REQUEST,
                            Map.of("pautaId", "Não há sessão para a pauta " + pautaId)
            );
        }

        return sessoes;
    }

    private boolean possuiSessoesAbertas(List<SessaoVotacao> sessoes) {
        return sessoes.stream().anyMatch(SessaoVotacao::isAberta);
    }

    private ResultadoVotacao calcularResultado(long totalSim, long totalNao) {
        if (totalSim > totalNao) return ResultadoVotacao.SIM;
        if (totalNao > totalSim) return ResultadoVotacao.NAO;
        return ResultadoVotacao.EMPATE;
    }
}
