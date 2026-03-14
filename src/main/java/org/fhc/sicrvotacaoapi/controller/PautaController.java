package org.fhc.sicrvotacaoapi.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.fhc.sicrvotacaoapi.dto.PautaRequestDTO;
import org.fhc.sicrvotacaoapi.dto.PautaResponseDTO;
import org.fhc.sicrvotacaoapi.service.PautaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/pautas")
public class PautaController {
    private final PautaService pautaService;

    public PautaController(PautaService pautaService) {
        this.pautaService = pautaService;
    }

    @PostMapping
    public ResponseEntity<PautaResponseDTO> criarPauta(@Valid @RequestBody PautaRequestDTO pautaRequest) {
        log.info("POST /api/v1/pautas chamado com nome={}", pautaRequest.nome());

        PautaResponseDTO pautaResponse = pautaService.criarPauta(pautaRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pautaResponse);
    }
}
