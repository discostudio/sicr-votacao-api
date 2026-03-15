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
import org.fhc.sicrvotacaoapi.dto.voto.VotoRequestDTO;
import org.fhc.sicrvotacaoapi.dto.voto.VotoResponseDTO;
import org.fhc.sicrvotacaoapi.service.VotoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Votos",
        description = "Registro dos votos dos associados nas pautas, em sessões abertas"
)
@Slf4j
@RestController
@RequestMapping("/api/v1/votos")
public class VotoController {

    private final VotoService votoService;

    public VotoController(VotoService votoService) {
        this.votoService = votoService;
    }

    @Operation(summary = "Registra um voto",
               description = "Registra o voto de um associado em uma pauta com sessão de votação aberta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                         description = "Voto registrado com sucesso",
                         content = @Content(schema = @Schema(implementation = VotoResponseDTO.class))),
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
                         description = "Não foi possível registrar o voto. Pauta ou sessão aberta não encontrada",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class),
                                 examples = @ExampleObject(
                                         value = """
                                         {
                                              "message": "Não foi possível registrar o voto.",
                                              "fieldErrors": {
                                                  "pautaId": "Não há sessão de votação aberta para a pauta com o ID 1"
                                              }
                                          }
                                        """
                                 )
                         )),
            @ApiResponse(responseCode = "409",
                         description = "Não foi possível registrar o voto. Associado já votou na pauta",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class),
                                 examples = @ExampleObject(
                                         value = """
                                         {
                                              "message": "Não foi possível registrar o voto.",
                                              "fieldErrors": {
                                                  "associadoId": "Já há um voto na pauta para o associado com o ID 10"
                                              }
                                          }
                                        """
                                 )
                         ))
    })
    @PostMapping
    public ResponseEntity<VotoResponseDTO> registrarVoto(@Valid @RequestBody VotoRequestDTO votoRequest) {
        log.info("VotoController: POST /api/v1/voto. Pauta: {}, Associado: {}", votoRequest.pautaId(), votoRequest.associadoCPF());

        VotoResponseDTO votoResponse = votoService.registrarVoto(votoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(votoResponse);
    }
}
