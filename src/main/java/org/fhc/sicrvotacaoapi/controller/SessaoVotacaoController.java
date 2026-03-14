package org.fhc.sicrvotacaoapi.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.fhc.sicrvotacaoapi.dto.sessao.SessaoRequestDTO;
import org.fhc.sicrvotacaoapi.dto.sessao.SessaoResponseDTO;
import org.fhc.sicrvotacaoapi.service.SessaoVotacaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/sessoes")
public class SessaoVotacaoController {

    private final SessaoVotacaoService sessaoService;

    public SessaoVotacaoController(SessaoVotacaoService sessaoService) {
        this.sessaoService = sessaoService;
    }

    @PostMapping
    public ResponseEntity<SessaoResponseDTO> abrirSessao(@Valid @RequestBody SessaoRequestDTO sessaoRequest) {
        log.info("POST /api/v1/sessoes chamado com pauta={}", sessaoRequest.pautaId());

        SessaoResponseDTO sessaoResponse = sessaoService.abrirSessao(sessaoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(sessaoResponse);
    }
}
