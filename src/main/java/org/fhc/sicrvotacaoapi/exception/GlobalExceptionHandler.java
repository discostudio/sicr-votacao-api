package org.fhc.sicrvotacaoapi.exception;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import lombok.extern.slf4j.Slf4j;
import org.fhc.sicrvotacaoapi.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // Validação de DTO
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> fieldErrors.put(err.getField(), err.getDefaultMessage()));

        ErrorResponseDTO error = new ErrorResponseDTO("Campos inválidos", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Campos desconhecidos no JSON
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleUnreadable(HttpMessageNotReadableException ex) {
        Map<String, String> fieldErrors = new HashMap<>();

        // verifica se a causa é UnrecognizedPropertyException
        Throwable cause = ex.getCause();
        if (cause instanceof com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException unknown) {
            fieldErrors.put(unknown.getPropertyName(), "Campo não reconhecido");
        }

        ErrorResponseDTO error = new ErrorResponseDTO(
                "JSON inválido",
                fieldErrors.isEmpty() ? null : fieldErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Tratamento da Sessão já aberta
    @ExceptionHandler(SessaoJaAbertaException.class)
    public ResponseEntity<ErrorResponseDTO> handleSessaoJaAberta(SessaoJaAbertaException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        fieldErrors.put("pautaId", ex.getMessage());

        ErrorResponseDTO error = new ErrorResponseDTO("Erro ao abrir sessão", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Tratamento de pauta não encontrada ao abrir a sessão
    @ExceptionHandler(PautaNaoEncontradaException.class)
    public ResponseEntity<ErrorResponseDTO> handlePautaNaoEncontrada(PautaNaoEncontradaException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        fieldErrors.put("pautaId", ex.getMessage());

        ErrorResponseDTO error = new ErrorResponseDTO("Erro ao abrir sessão", fieldErrors);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // Sessão não encontrada
    @ExceptionHandler(SessaoNaoEncontradaException.class)
    public ResponseEntity<ErrorResponseDTO> handleSessaoNaoEncontrada(SessaoNaoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDTO(ex.getMessage(), Map.of()));
    }

    // Sessão fechada
    @ExceptionHandler(SessaoFechadaException.class)
    public ResponseEntity<ErrorResponseDTO> handleSessaoFechada(SessaoFechadaException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDTO(ex.getMessage(), Map.of()));
    }

    // Associado já votou
    @ExceptionHandler(AssociadoJaVotouException.class)
    public ResponseEntity<ErrorResponseDTO> handleAssociadoJaVotou(AssociadoJaVotouException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDTO(ex.getMessage(), Map.of()));
    }

    // Voto inválido
    @ExceptionHandler(VotoInvalidoException.class)
    public ResponseEntity<ErrorResponseDTO> handleVotoInvalido(VotoInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDTO(ex.getMessage(), Map.of()));
    }

    // Erros genéricos
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneric(Exception ex) {
        ErrorResponseDTO error = new ErrorResponseDTO("Erro interno do servidor", null);
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneric(RuntimeException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO("Erro interno do servidor", null);
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
