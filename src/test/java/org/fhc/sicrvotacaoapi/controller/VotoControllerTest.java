package org.fhc.sicrvotacaoapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.fhc.sicrvotacaoapi.dto.voto.VotoRequestDTO;
import org.fhc.sicrvotacaoapi.model.VotoValor;
import org.fhc.sicrvotacaoapi.service.VotoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VotoController.class)
public class VotoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // Para converter objeto em JSON

    @MockitoBean
    private VotoService votoService; // Mocka o service para não tocar no banco

    @Test
    @DisplayName("Deve retornar 201 ao registrar voto com sucesso")
    void deveRegistrarVotoComSucesso() throws Exception {
        // GIVEN
        VotoRequestDTO request = new VotoRequestDTO(1L, "12345678900", VotoValor.SIM);

        // WHEN & THEN
        mockMvc.perform(post("/api/v1/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Deve retornar 400 quando o CPF for inválido (Validation)")
    void deveRetornarErroValidacaoCpf() throws Exception {
        // GIVEN - CPF com apenas 3 dígitos (ferindo o @Pattern do DTO)
        VotoRequestDTO request = new VotoRequestDTO(1L, "123", VotoValor.SIM);

        // WHEN & THEN
        mockMvc.perform(post("/api/v1/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        // Service barra
    }
}
