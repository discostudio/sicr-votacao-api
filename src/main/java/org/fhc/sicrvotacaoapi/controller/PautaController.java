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
import org.fhc.sicrvotacaoapi.dto.pauta.PautaRequestDTO;
import org.fhc.sicrvotacaoapi.dto.pauta.PautaResponseDTO;
import org.fhc.sicrvotacaoapi.dto.resultado.ResultadoVotacaoConsolidadoDTO;
import org.fhc.sicrvotacaoapi.dto.resultado.ResultadoVotacaoDTO;
import org.fhc.sicrvotacaoapi.service.PautaService;
import org.fhc.sicrvotacaoapi.service.ResultadoVotacaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Pautas",
        description = "Criação de pautas e consulta de resultados de votação das pautas"
)
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

    @Operation(summary = "Cria uma nova pauta",
               description = "Recebe um objeto PautaRequestDTO e cria uma nova pauta de votação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                         description = "Pauta criada com sucesso",
                         content = @Content(schema = @Schema(implementation = PautaRequestDTO.class))),
            @ApiResponse(responseCode = "400",
                         description = "Requisição inválida",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class),
                                 examples = @ExampleObject(
                                         value = """
                                         {
                                             "message": "Campos inválidos",
                                             "fieldErrors": {
                                                 "nome": "O nome da pauta é obrigatório"
                                             }
                                         }
                                        """
                                 )
                         ))
    })
    @PostMapping
    public ResponseEntity<PautaResponseDTO> criarPauta(@Valid @RequestBody PautaRequestDTO pautaRequest) {
        log.info("POST /api/v1/pautas chamado com nome={}", pautaRequest.nome());

        PautaResponseDTO pautaResponse = pautaService.criarPauta(pautaRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pautaResponse);
    }

    @Operation(summary = "Consulta o resultado consolidado de uma pauta",
               description = "Retorna o resultado consolidado da votação de uma pauta específica, inclusive detalhando o resultado das sessões")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                         description = "Resultado retornado com sucesso",
                         content = @Content(schema = @Schema(implementation = ResultadoVotacaoConsolidadoDTO.class))),
            @ApiResponse(responseCode = "400",
                         description = "Requisição inválida",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class),
                                 examples = @ExampleObject(
                                         value = """
                                         {
                                              "message": "Erro de validação de parâmetro",
                                              "fieldErrors": {
                                                  "pautaId": "O valor 'A' é inválido. Esperava-se um tipo Long."
                                              }
                                          }
                                        """
                                 )
                         )),
            @ApiResponse(responseCode = "404",
                         description = "Resultado não encontrado",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class),
                                 examples = @ExampleObject(
                                         value = """
                                         {
                                              "message": "Resultado não encontrado.",
                                              "fieldErrors": {
                                                  "pautaId": "Não há pauta com o ID 15"
                                              }
                                          }
                                        """
                                 )
                         ))
    })
    @GetMapping("/{pautaId}/resultadoConsolidado")
    public ResponseEntity<ResultadoVotacaoConsolidadoDTO> obterResultadoConsolidado(@PathVariable Long pautaId) {
        ResultadoVotacaoConsolidadoDTO resultadoVotacao = resultadoService.obterResultadoConsolidado(pautaId);
        return ResponseEntity.ok(resultadoVotacao);
    }

    @Operation(summary = "Consulta o resultado de uma pauta",
               description = "Retorna o resultado da votação de uma pauta específica, sem detalhes adicionais")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                         description = "Resultado retornado com sucesso",
                         content = @Content(schema = @Schema(implementation = ResultadoVotacaoConsolidadoDTO.class))),
            @ApiResponse(responseCode = "400",
                         description = "Requisição inválida",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class),
                                 examples = @ExampleObject(
                                         value = """
                                         {
                                              "message": "Erro de validação de parâmetro",
                                              "fieldErrors": {
                                                  "pautaId": "O valor 'A' é inválido. Esperava-se um tipo Long."
                                              }
                                          }
                                        """
                                 )
                         )),
            @ApiResponse(responseCode = "404",
                         description = "Pauta não encontrada",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class),
                                 examples = @ExampleObject(
                                         value = """
                                         {
                                              "message": "Resultado não encontrado.",
                                              "fieldErrors": {
                                                  "pautaId": "Não há pauta com o ID 15"
                                              }
                                          }
                                        """
                                 )
                         ))
    })
    @GetMapping("/{pautaId}/resultado")
    public ResponseEntity<ResultadoVotacaoDTO> obterResultado(@PathVariable Long pautaId) {
        ResultadoVotacaoDTO resultadoVotacao = resultadoService.obterResultado(pautaId);
        return ResponseEntity.ok(resultadoVotacao);
    }
}
