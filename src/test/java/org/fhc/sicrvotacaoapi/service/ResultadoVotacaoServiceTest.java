package org.fhc.sicrvotacaoapi.service;

import org.fhc.sicrvotacaoapi.dto.resultado.ResultadoVotacaoDetalhadoDTO;
import org.fhc.sicrvotacaoapi.exception.BusinessException;
import org.fhc.sicrvotacaoapi.model.Pauta;
import org.fhc.sicrvotacaoapi.model.ResultadoVotacao;
import org.fhc.sicrvotacaoapi.model.SessaoVotacao;
import org.fhc.sicrvotacaoapi.model.VotoValor;
import org.fhc.sicrvotacaoapi.repository.PautaRepository;
import org.fhc.sicrvotacaoapi.repository.SessaoVotacaoRepository;
import org.fhc.sicrvotacaoapi.repository.VotoRepository;
import org.fhc.sicrvotacaoapi.utils.TestEntityFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResultadoVotacaoServiceTest {

    @Mock
    private SessaoVotacaoRepository sessaoRepository;

    @Mock
    private VotoRepository votoRepository;

    @Mock
    private PautaRepository pautaRepository;

    @Mock
    private SessaoVotacaoService sessaoService;

    @InjectMocks
    private ResultadoVotacaoService resultadoService;

    @Test
    @DisplayName("Retornar resultado detalhado com vitória do SIM")
    void deveRetornarResultadoDetalhadoComSucesso() {
        // GIVEN
        Long pautaId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        Pauta pauta = TestEntityFactory.criarPauta(pautaId, "Pauta Teste");
        SessaoVotacao sessao = TestEntityFactory.criarSessao(pauta, 1);

        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));
        when(sessaoRepository.findAllByPautaIdOrderByFimAsc(eq(pautaId), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(sessao)));

        // Mock da query de totais gerais
        when(votoRepository.countVotosGroupByValor(pautaId))
                .thenReturn(List.of(
                        new Object[]{VotoValor.SIM, 3L},
                        new Object[]{VotoValor.NAO, 1L}
                ));

        // Mock da query de totais por sessão
        when(votoRepository.countVotosAgrupadosPorSessoes(List.of(1L)))
                .thenReturn(List.of(
                        new Object[]{1L, VotoValor.SIM, 3L},
                        new Object[]{1L, VotoValor.NAO, 1L}
                ));

        // WHEN
        ResultadoVotacaoDetalhadoDTO resultado =
                resultadoService.obterResultadoDetalhado(pautaId, pageable);

        // THEN
        assertNotNull(resultado);
        assertEquals(3L, resultado.totalSim());
        assertEquals(1L, resultado.totalNao());
        assertEquals(4L, resultado.totalVotos());
        assertEquals(ResultadoVotacao.SIM, resultado.resultado());
    }

    @Test
    @DisplayName("Lançar exceção quando não há votos")
    void deveLancarExcecaoQuandoNaoHaVotos() {
        // GIVEN
        Long pautaId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        Pauta pauta = TestEntityFactory.criarPauta(pautaId, "Pauta Sem Votos");
        SessaoVotacao sessao = TestEntityFactory.criarSessao(pauta, 1);

        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));
        lenient().when(sessaoRepository.findAllByPautaIdOrderByFimAsc(eq(pautaId), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(sessao)));

        // Totais gerais zerados
        when(votoRepository.countVotosGroupByValor(pautaId)).thenReturn(Collections.emptyList());

        // WHEN & THEN
        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> resultadoService.obterResultadoDetalhado(pautaId, pageable)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertTrue(ex.getMessage().contains("Resultado não encontrado"));
    }

    @Test
    @DisplayName("Retornar EMPATE quando votos forem iguais")
    void deveRetornarEmpate() {
        // GIVEN
        Long pautaId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        Pauta pauta = TestEntityFactory.criarPauta(pautaId, "Pauta Empatada");
        SessaoVotacao sessao = TestEntityFactory.criarSessao(pauta, 1);

        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));
        when(sessaoRepository.findAllByPautaIdOrderByFimAsc(eq(pautaId), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(sessao)));

        // Totais gerais empatados
        when(votoRepository.countVotosGroupByValor(pautaId))
                .thenReturn(List.of(
                        new Object[]{VotoValor.SIM, 1L},
                        new Object[]{VotoValor.NAO, 1L}
                ));

        // Totais por sessão
        when(votoRepository.countVotosAgrupadosPorSessoes(List.of(1L)))
                .thenReturn(List.of(
                        new Object[]{1L, VotoValor.SIM, 1L},
                        new Object[]{1L, VotoValor.NAO, 1L}
                ));

        // WHEN
        ResultadoVotacaoDetalhadoDTO resultado =
                resultadoService.obterResultadoDetalhado(pautaId, pageable);

        // THEN
        assertEquals(ResultadoVotacao.EMPATE, resultado.resultado());
    }
}
