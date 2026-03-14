package org.fhc.sicrvotacaoapi.exception;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import lombok.extern.slf4j.Slf4j;
import org.fhc.sicrvotacaoapi.dto.ErrorResponseDTO;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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

    // parâmetro inválido
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Map<String, String> fieldErrors = new HashMap<>();

        // Usa o nome do parâmetro (ex: "id") e cria a mensagem amigável
        String fieldName = ex.getName();
        String requiredType = (ex.getRequiredType() != null) ? ex.getRequiredType().getSimpleName() : "definido";
        String errorMessage = String.format("O valor '%s' é inválido. Esperava-se um tipo %s.", ex.getValue(), requiredType);

        fieldErrors.put(fieldName, errorMessage);

        ErrorResponseDTO error = new ErrorResponseDTO("Erro de validação de parâmetro", fieldErrors);

        // Retornamos 400 (Bad Request) porque o erro foi no envio do dado pelo cliente
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Rota inválida
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNoResourceFound(NoResourceFoundException ex) {
        Map<String, String> fieldErrors = new HashMap<>();

        // Identifica qual recurso/rota o usuário tentou acessar
        String resourcePath = ex.getResourcePath();

        fieldErrors.put("url", "O recurso solicitado '" + resourcePath + "' não foi encontrado.");
        fieldErrors.put("dica", "Verifique se você esqueceu de passar o ID ou parâmetro obrigatório na URL.");

        ErrorResponseDTO error = new ErrorResponseDTO("Recurso não encontrado", fieldErrors);

        // Para NoResourceFound, o status correto é 404 NOT_FOUND
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
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

    // Tratamento de pauta não encontrada
    @ExceptionHandler(PautaNaoEncontradaException.class)
    public ResponseEntity<ErrorResponseDTO> handlePautaNaoEncontrada(PautaNaoEncontradaException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        fieldErrors.put("pautaId", ex.getMessage());

        ErrorResponseDTO error = new ErrorResponseDTO("Erro ao realizar operação.", fieldErrors);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // Sessão não encontrada
    @ExceptionHandler(SessaoNaoEncontradaException.class)
    public ResponseEntity<ErrorResponseDTO> handleSessaoNaoEncontrada(SessaoNaoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDTO(ex.getMessage(), Map.of()));
    }

    // Pauta sem sessões abertas
    @ExceptionHandler(PautaSemSessoesAbertasException.class)
    public ResponseEntity<ErrorResponseDTO> handlePautaSemSessoesAbertas(PautaSemSessoesAbertasException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDTO(ex.getMessage(), Map.of()));
    }

    // Pauta sem sessões
    @ExceptionHandler(PautaSemSessoesException.class)
    public ResponseEntity<ErrorResponseDTO> handlePautaSemSessoes(PautaSemSessoesException ex) {
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

    // Erro de integridade no banco - importante para casos de unique constraints (tabela Voto, por exemplo)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleDataIntegrityViolation(DataIntegrityViolationException ex) {

        log.error("Erro de integridade no banco", ex);

        ErrorResponseDTO error = new ErrorResponseDTO(
                "Erro ao armazenar informação",
                Map.of("erro", "Operação não pôde ser concluída devido a conflito de dados")
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
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
