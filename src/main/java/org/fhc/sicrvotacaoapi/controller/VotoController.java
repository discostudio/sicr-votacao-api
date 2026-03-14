package org.fhc.sicrvotacaoapi.controller;

import jakarta.validation.Valid;
import org.fhc.sicrvotacaoapi.dto.VotoRequestDTO;
import org.fhc.sicrvotacaoapi.dto.VotoResponseDTO;
import org.fhc.sicrvotacaoapi.service.VotoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/votos")
public class VotoController {

    private final VotoService votoService;

    public VotoController(VotoService votoService) {
        this.votoService = votoService;
    }

    @PostMapping
    public ResponseEntity<VotoResponseDTO> registrarVoto(@Valid @RequestBody VotoRequestDTO votoRequest) {
        VotoResponseDTO votoResponse = votoService.registrarVoto(votoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(votoResponse);
    }
}
