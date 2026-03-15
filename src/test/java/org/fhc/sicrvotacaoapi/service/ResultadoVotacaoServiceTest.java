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
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResultadoVotacaoServiceTest {

    @Mock
    private SessaoVotacaoRepository sessaoRepository;
    @Mock private VotoRepository votoRepository;
    @Mock private PautaRepository pautaRepository;

    @InjectMocks
    private ResultadoVotacaoService resultadoService;

    @Test
    @DisplayName("Retornar resultado detalhado com vitória do SIM")
    void deveRetornarResultadoDetalhadoComSucesso() {
        // GIVEN
        Long pautaId = 1L;
        Pauta pauta = TestEntityFactory.criarPauta(pautaId, "Pauta Teste");
        SessaoVotacao sessao = TestEntityFactory.criarSessao(pauta, 1);

        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));
        when(sessaoRepository.findAllByPautaIdOrderByFimAsc(pautaId)).thenReturn(List.of(sessao));

        // Mock das contagens: 3 SIM e 1 NAO
        when(votoRepository.countBySessaoId(1L)).thenReturn(4L); // total de votos na pauta
        when(votoRepository.countBySessaoIdAndValor(1L, VotoValor.SIM)).thenReturn(3L);
        when(votoRepository.countBySessaoIdAndValor(1L, VotoValor.NAO)).thenReturn(1L);

        // WHEN
        ResultadoVotacaoDetalhadoDTO resultado = resultadoService.obterResultadoDetalhado(pautaId);

        // THEN
        assertNotNull(resultado);
        assertEquals(3L, resultado.totalSim());
        assertEquals(1L, resultado.totalNao());
        assertEquals(4L, resultado.totalVotos());
        assertEquals(ResultadoVotacao.SIM, resultado.resultado());
    }

    @Test
    @DisplayName("Lançar exceção quando a pauta não tem votos")
    void deveLancarExcecaoQuandoNaoHaVotos() {
        // GIVEN
        Long pautaId = 1L;
        Pauta pauta = TestEntityFactory.criarPauta(pautaId, "Pauta Sem Votos");
        SessaoVotacao sessao = TestEntityFactory.criarSessao(pauta, 1);

        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));
        when(sessaoRepository.findAllByPautaIdOrderByFimAsc(pautaId)).thenReturn(List.of(sessao));

        // Simula que o total de votos é ZERO
        when(votoRepository.countBySessaoId(1L)).thenReturn(0L);

        // WHEN & THEN
        BusinessException ex = assertThrows(BusinessException.class,
                () -> resultadoService.obterResultadoDetalhado(pautaId));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertTrue(ex.getMessage().contains("Resultado não encontrado"));
    }

    @Test
    @DisplayName("Retornar EMPATE quando votos forem iguais")
    void deveRetornarEmpate() {
        // GIVEN
        Long pautaId = 1L;
        Pauta pauta = TestEntityFactory.criarPauta(pautaId, "Pauta Empatada");
        SessaoVotacao sessao = TestEntityFactory.criarSessao(pauta, 1);

        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));
        when(sessaoRepository.findAllByPautaIdOrderByFimAsc(pautaId)).thenReturn(List.of(sessao));
        when(votoRepository.countBySessaoId(1L)).thenReturn(2L);
        when(votoRepository.countBySessaoIdAndValor(1L, VotoValor.SIM)).thenReturn(1L);
        when(votoRepository.countBySessaoIdAndValor(1L, VotoValor.NAO)).thenReturn(1L);

        // WHEN
        ResultadoVotacaoDetalhadoDTO resultado = resultadoService.obterResultadoDetalhado(pautaId);

        // THEN
        assertEquals(ResultadoVotacao.EMPATE, resultado.resultado());
    }
}
