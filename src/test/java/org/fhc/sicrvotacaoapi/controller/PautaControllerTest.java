package org.fhc.sicrvotacaoapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.fhc.sicrvotacaoapi.dto.pauta.PautaRequestDTO;
import org.fhc.sicrvotacaoapi.dto.pauta.PautaResponseDTO;
import org.fhc.sicrvotacaoapi.dto.resultado.ResultadoVotacaoDTO;
import org.fhc.sicrvotacaoapi.model.ResultadoVotacao;
import org.fhc.sicrvotacaoapi.service.PautaService;
import org.fhc.sicrvotacaoapi.service.ResultadoVotacaoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PautaController.class)
class PautaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PautaService pautaService;

    @MockitoBean
    private ResultadoVotacaoService resultadoService;

    @Test
    @DisplayName("Deve criar uma pauta com sucesso e retornar 201")
    void deveCriarPautaComSucesso() throws Exception {
        PautaRequestDTO request = new PautaRequestDTO("Pauta Teste", "Descrição");
        PautaResponseDTO response = new PautaResponseDTO(1L, "Pauta Teste", "Descrição");

        when(pautaService.criarPauta(any(PautaRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/pautas") // URL completa conforme seu @RequestMapping
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Pauta Teste"));
    }

    @Test
    @DisplayName("Deve retornar o resultado simplificado da pauta")
    void deveRetornarResultadoSimplificado() throws Exception {
        ResultadoVotacaoDTO resultadoDTO = new ResultadoVotacaoDTO(1L, ResultadoVotacao.SIM, false);

        when(resultadoService.obterResultado(anyLong())).thenReturn(resultadoDTO);

        mockMvc.perform(get("/api/v1/pautas/1/resultado")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pautaId").value(1))
                .andExpect(jsonPath("$.resultado").value("SIM"));
    }
}
