package org.fhc.sicrvotacaoapi.service;

import org.fhc.sicrvotacaoapi.dto.sessao.SessaoRequestDTO;
import org.fhc.sicrvotacaoapi.dto.sessao.SessaoResponseDTO;
import org.fhc.sicrvotacaoapi.exception.BusinessException;
import org.fhc.sicrvotacaoapi.model.Pauta;
import org.fhc.sicrvotacaoapi.model.SessaoVotacao;
import org.fhc.sicrvotacaoapi.repository.PautaRepository;
import org.fhc.sicrvotacaoapi.repository.SessaoVotacaoRepository;
import org.fhc.sicrvotacaoapi.utils.TestEntityFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SessaoVotacaoServiceTest {

    @Mock private SessaoVotacaoRepository sessaoRepository;
    @Mock private PautaRepository pautaRepository;
    @InjectMocks private SessaoVotacaoService sessaoService;

    @Test
    @DisplayName("Abrir sessão com duração padrão de 1 minuto quando duração for nula")
    void deveAbrirSessaoComDuracaoPadrao() {
        // GIVEN
        Long pautaId = 1L;
        SessaoRequestDTO request = new SessaoRequestDTO(pautaId, null);
        Pauta pauta = TestEntityFactory.criarPauta(pautaId, "Pauta Teste");

        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));
        when(sessaoRepository.existsByPautaIdAndFimAfter(eq(pautaId), any(LocalDateTime.class))).thenReturn(false);

        // Simular o retorno com ID 100
        when(sessaoRepository.save(any(SessaoVotacao.class))).thenAnswer(invocation -> {
            SessaoVotacao s = invocation.getArgument(0);
            ReflectionTestUtils.setField(s, "id", 100L); // ID gerado pelo "banco"
            return s;
        });

        // WHEN
        SessaoResponseDTO response = sessaoService.abrirSessao(request);

        // THEN
        assertNotNull(response);
        assertEquals(100L, response.id());
        assertTrue(response.fim().isAfter(response.inicio()));

        long minutos = Duration.between(response.inicio(), response.fim()).toMinutes();
        assertEquals(1, minutos, "A duração padrão deve ser de 1 minuto");

        verify(sessaoRepository).save(any(SessaoVotacao.class));
    }

    @Test
    @DisplayName("Lançar exceção quando a pauta não existe")
    void deveLancarExcecaoPautaNaoEncontrada() {
        // GIVEN
        SessaoRequestDTO request = new SessaoRequestDTO(99L, 5);
        when(pautaRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(BusinessException.class, () -> sessaoService.abrirSessao(request));
        verify(sessaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Lançar exceção quando já existe sessão ativa para a pauta")
    void deveLancarExcecaoSessaoJaExistente() {
        // GIVEN
        Long pautaId = 1L;
        SessaoRequestDTO request = new SessaoRequestDTO(pautaId, 5);
        Pauta pauta = TestEntityFactory.criarPauta(pautaId, "Pauta Existente");

        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));
        when(sessaoRepository.existsByPautaIdAndFimAfter(eq(pautaId), any(LocalDateTime.class))).thenReturn(true);

        // WHEN
        BusinessException ex = assertThrows(BusinessException.class, () -> sessaoService.abrirSessao(request));

        // THEN
        assertTrue(ex.getMessage().contains("Não foi possível criar a sessão"));
        assertNotNull(ex.getFieldErrors());
        assertEquals("Já existe uma sessão aberta para a pauta com ID 1", ex.getFieldErrors().get("pautaId"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }
}
