package org.fhc.sicrvotacaoapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.fhc.sicrvotacaoapi.dto.sessao.SessaoRequestDTO;
import org.fhc.sicrvotacaoapi.dto.sessao.SessaoResponseDTO;
import org.fhc.sicrvotacaoapi.service.SessaoVotacaoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SessaoVotacaoController.class)
public class SessaoVotacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SessaoVotacaoService sessaoService;

    @Test
    @DisplayName("Deve abrir uma sessão de votação com sucesso e retornar 201")
    void deveAbrirSessaoComSucesso() throws Exception {
        // GIVEN
        Long pautaId = 1L;
        // Request para abrir sessão por 1 minutos
        SessaoRequestDTO request = new SessaoRequestDTO(pautaId, 1);

        SessaoResponseDTO response = new SessaoResponseDTO(
                100L,
                pautaId,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(1)
        );

        when(sessaoService.abrirSessao(any(SessaoRequestDTO.class))).thenReturn(response);

        // WHEN & THEN
        mockMvc.perform(post("/api/v1/sessoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.pautaId").value(pautaId));
    }

    @Test
    @DisplayName("Deve retornar 400 quando o pautaId for nulo")
    void deveRetornarErroValidacaoPautaIdNulo() throws Exception {
        // GIVEN - ferindo o @NotNull do seu DTO
        SessaoRequestDTO request = new SessaoRequestDTO(null, 1);

        // WHEN & THEN
        mockMvc.perform(post("/api/v1/sessoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
