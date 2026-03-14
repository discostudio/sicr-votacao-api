package org.fhc.sicrvotacaoapi.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.fhc.sicrvotacaoapi.dto.PautaRequestDTO;
import org.fhc.sicrvotacaoapi.dto.PautaResponseDTO;
import org.fhc.sicrvotacaoapi.dto.ResultadoVotacaoConsolidadoDTO;
import org.fhc.sicrvotacaoapi.dto.ResultadoVotacaoDTO;
import org.fhc.sicrvotacaoapi.service.PautaService;
import org.fhc.sicrvotacaoapi.service.ResultadoVotacaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/pautas")
public class PautaController {
    private final PautaService pautaService;
    private final ResultadoVotacaoService resultadoService;

    public PautaController(PautaService pautaService, ResultadoVotacaoService resultadoService) {
        this.pautaService = pautaService;
        this.resultadoService = resultadoService;
    }

    @PostMapping
    public ResponseEntity<PautaResponseDTO> criarPauta(@Valid @RequestBody PautaRequestDTO pautaRequest) {
        log.info("POST /api/v1/pautas chamado com nome={}", pautaRequest.nome());

        PautaResponseDTO pautaResponse = pautaService.criarPauta(pautaRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pautaResponse);
    }

    @GetMapping("/{pautaId}/resultadoConsolidado")
    public ResponseEntity<ResultadoVotacaoConsolidadoDTO> obterResultadoConsolidado(@PathVariable Long pautaId) {
        ResultadoVotacaoConsolidadoDTO resultadoVotacao = resultadoService.obterResultadoConsolidado(pautaId);
        return ResponseEntity.ok(resultadoVotacao);
    }

    @GetMapping("/{pautaId}/resultado")
    public ResponseEntity<ResultadoVotacaoDTO> obterResultado(@PathVariable Long pautaId) {
        ResultadoVotacaoDTO resultadoVotacao = resultadoService.obterResultado(pautaId);
        return ResponseEntity.ok(resultadoVotacao);
    }
}
