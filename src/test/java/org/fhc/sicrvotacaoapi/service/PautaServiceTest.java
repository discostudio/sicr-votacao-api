package org.fhc.sicrvotacaoapi.service;

import org.fhc.sicrvotacaoapi.dto.pauta.PautaRequestDTO;
import org.fhc.sicrvotacaoapi.dto.pauta.PautaResponseDTO;
import org.fhc.sicrvotacaoapi.model.Pauta;
import org.fhc.sicrvotacaoapi.repository.PautaRepository;
import org.fhc.sicrvotacaoapi.utils.TestEntityFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PautaServiceTest {

    @Mock
    private PautaRepository pautaRepository;

    @InjectMocks
    private PautaService pautaService;

    @Test
    @DisplayName("Criar uma pauta com sucesso e retornar o DTO correto")
    void deveCriarPautaComSucesso() {
        // 1. GIVEN - Preparação usando a Factory
        PautaRequestDTO request = new PautaRequestDTO("Aumento do VR", "Votação para reajuste anual");

        // Criamos a entidade já com o ID simulado através da nossa Factory
        Pauta pautaSalva = TestEntityFactory.criarPauta(1L, request.nome());

        // Configuramos o Mock
        when(pautaRepository.save(any(Pauta.class))).thenReturn(pautaSalva);

        // 2. WHEN - Execução
        PautaResponseDTO response = pautaService.criarPauta(request);

        // 3. THEN - Validações
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Aumento do VR", response.nome());

        verify(pautaRepository, times(1)).save(any(Pauta.class));
    }
}
