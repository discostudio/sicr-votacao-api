package org.fhc.sicrvotacaoapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.fhc.sicrvotacaoapi.dto.error.ErrorResponseDTO;
import org.fhc.sicrvotacaoapi.dto.sessao.SessaoRequestDTO;
import org.fhc.sicrvotacaoapi.dto.sessao.SessaoResponseDTO;
import org.fhc.sicrvotacaoapi.service.SessaoVotacaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Sessões de Votação",
        description = "Criação de sessões de votação para pautas existentes"
)
@Slf4j
@RestController
@RequestMapping("/api/v1/sessoes")
public class SessaoVotacaoController {

    private final SessaoVotacaoService sessaoService;

    public SessaoVotacaoController(SessaoVotacaoService sessaoService) {
        this.sessaoService = sessaoService;
    }

    @Operation(summary = "Cria uma nova sessão de votação",
               description = "Recebe um objeto SessaoRequestDTO e cria uma nova sessão de votação para uma pauta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                         description = "Sessão de votação criada com sucesso",
                         content = @Content(schema = @Schema(implementation = SessaoResponseDTO.class))),
            @ApiResponse(responseCode = "400",
                         description = "Requisição inválida",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class),
                                 examples = @ExampleObject(
                                         value = """
                                         {
                                              "message": "JSON inválido",
                                              "fieldErrors": {
                                                  "pauta": "Campo não reconhecido"
                                              }
                                          }
                                        """
                                 )
                         )),
            @ApiResponse(responseCode = "404",
                         description = "Não foi possível criar a sessão. Pauta ou sessão não encontrada",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class),
                                 examples = @ExampleObject(
                                         value = """
                                         {
                                              "message": "Não foi possível criar a sessão",
                                              "fieldErrors": {
                                                  "pautaId": "Pauta não encontrada com o ID 10"
                                              }
                                          }
                                        """
                                 )
                         ))
    })
    @PostMapping
    public ResponseEntity<SessaoResponseDTO> abrirSessao(@Valid @RequestBody SessaoRequestDTO sessaoRequest) {
        log.info("SessaoController: POST /api/v1/sessoes. Pauta: {}.", sessaoRequest.pautaId());

        SessaoResponseDTO sessaoResponse = sessaoService.abrirSessao(sessaoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(sessaoResponse);
    }
}
