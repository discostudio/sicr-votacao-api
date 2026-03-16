package org.fhc.sicrvotacaoapi.service;

import org.fhc.sicrvotacaoapi.dto.voto.VotoRequestDTO;
import org.fhc.sicrvotacaoapi.dto.voto.VotoResponseDTO;
import org.fhc.sicrvotacaoapi.exception.BusinessException;
import org.fhc.sicrvotacaoapi.model.Pauta;
import org.fhc.sicrvotacaoapi.model.SessaoVotacao;
import org.fhc.sicrvotacaoapi.model.Voto;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VotoServiceTest {

    @Mock private VotoRepository votoRepository;
    @Mock private SessaoVotacaoRepository sessaoRepository;
    @Mock private PautaRepository pautaRepository;
    @Mock private CPFValidacaoService cpfValidacaoService;

    @InjectMocks
    private VotoService votoService;

    @Test
    @DisplayName("Registrar voto SIM com sucesso")
    void deveRegistrarVotoSimComSucesso() {
        // GIVEN
        VotoRequestDTO request = new VotoRequestDTO(1L, "12345678901", VotoValor.SIM);
        Pauta pauta = TestEntityFactory.criarPauta(1L, "Pauta Teste");
        SessaoVotacao sessao = TestEntityFactory.criarSessao(pauta, 1);

        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(sessaoRepository.findByPautaIdAndFimAfter(eq(1L), any(LocalDateTime.class)))
                .thenReturn(Optional.of(sessao));
        when(cpfValidacaoService.podeVotar("12345678901")).thenReturn(true);
        when(votoRepository.existsBySessaoPautaIdAndAssociadoCpf(1L, "12345678901")).thenReturn(false);
        when(votoRepository.save(any(Voto.class))).thenAnswer(i -> {
            Voto v = i.getArgument(0);
            ReflectionTestUtils.setField(v, "id", 500L);
            return v;
        });

        // WHEN
        VotoResponseDTO response = votoService.registrarVoto(request);

        // THEN
        assertNotNull(response);
        assertEquals(VotoValor.SIM, response.valor());
        verify(votoRepository).save(any());
    }

    @Test
    @DisplayName("Registrar voto NAO com sucesso")
    void deveRegistrarVotoNaoComSucesso() {
        // GIVEN
        VotoRequestDTO request = new VotoRequestDTO(1L, "12345678901", VotoValor.NAO);
        Pauta pauta = TestEntityFactory.criarPauta(1L, "Pauta Teste");
        SessaoVotacao sessao = TestEntityFactory.criarSessao(pauta, 1);

        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(sessaoRepository.findByPautaIdAndFimAfter(eq(1L), any(LocalDateTime.class)))
                .thenReturn(Optional.of(sessao));
        when(cpfValidacaoService.podeVotar("12345678901")).thenReturn(true);
        when(votoRepository.existsBySessaoPautaIdAndAssociadoCpf(1L, "12345678901")).thenReturn(false);
        when(votoRepository.save(any(Voto.class))).thenAnswer(i -> {
            Voto v = i.getArgument(0);
            ReflectionTestUtils.setField(v, "id", 501L);
            return v;
        });

        // WHEN
        VotoResponseDTO response = votoService.registrarVoto(request);

        // THEN
        assertNotNull(response);
        assertEquals(VotoValor.NAO, response.valor());
        verify(votoRepository).save(any());
    }

    @Test
    @DisplayName("Lançar erro quando não há pauta com ID informado")
    void deveErroQuandoPautaNaoExiste() {
        // GIVEN
        VotoRequestDTO request = new VotoRequestDTO(99L, "12345678901", VotoValor.SIM);

        when(pautaRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN & THEN
        BusinessException ex = assertThrows(BusinessException.class,
                () -> votoService.registrarVoto(request));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertTrue(ex.getFieldErrors().get("pautaId").contains("Pauta não encontrada"));
        verify(votoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Lançar erro quando não há sessão aberta para votar")
    void deveErroQuandoSessaoNaoAberta() {
        // GIVEN
        VotoRequestDTO request = new VotoRequestDTO(1L, "12345678901", VotoValor.SIM);
        Pauta pauta = TestEntityFactory.criarPauta(1L, "Pauta Teste");

        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(sessaoRepository.findByPautaIdAndFimAfter(eq(1L), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        when(cpfValidacaoService.podeVotar("12345678901")).thenReturn(true);
        when(votoRepository.existsBySessaoPautaIdAndAssociadoCpf(1L, "12345678901")).thenReturn(false);

        // WHEN & THEN
        BusinessException ex = assertThrows(BusinessException.class,
                () -> votoService.registrarVoto(request));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertTrue(ex.getFieldErrors().get("pautaId").contains("Não há sessão de votação aberta"));
        verify(votoRepository, never()).save(any());
    }
}
