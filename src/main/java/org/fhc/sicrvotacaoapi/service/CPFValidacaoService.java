package org.fhc.sicrvotacaoapi.service;

import lombok.extern.slf4j.Slf4j;
import org.fhc.sicrvotacaoapi.configuration.CPFValidacaoConfig;
import org.fhc.sicrvotacaoapi.exception.BusinessException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Service
public class CPFValidacaoService {

    private final WebClient webClient;
    private final CPFValidacaoConfig cpfValidacaoConfig;

    // ⬅ injeta o bean pelo nome do método @Bean da config
    public CPFValidacaoService(@Qualifier("cpfValidatorWebClient") WebClient webClient,
                               CPFValidacaoConfig cpfValidacaoConfig) {
        this.webClient = webClient;
        this.cpfValidacaoConfig = cpfValidacaoConfig;
    }

    public boolean podeVotar(String cpf) {
        // Feature Toggle: Se estiver desativado nas propriedades, usa o Mock direto
        if (!cpfValidacaoConfig.isEnabled()) {
            log.info("Validação de CPF: desabilitada.");
            return true;
        }

        log.info("Validação de CPF: habilitada.");

        try {
            return executarChamadaExterna(cpf);
        } catch (Exception e) {
            // Fallback: Se a API falhar (Timeout, 404 de infra/HTML, 500, etc)
            // e a configuração permitir, usamos o Mock para não travar a votação
            if (cpfValidacaoConfig.isUseMockOnFailure()) {
                return validarViaMock(cpf);
            }

            // Caso contrário, propagamos o erro para o usuário
            if (e instanceof BusinessException) {
                throw (BusinessException) e;
            }

            throw new BusinessException(
                    "Serviço de validação de CPF indisponível",
                    HttpStatus.SERVICE_UNAVAILABLE,
                    Map.of("erro", e.getMessage())
            );
        }
    }

    private boolean executarChamadaExterna(String cpf) {
        log.info("Validação de CPF: chamada externa.");

        return webClient.get()
                .uri("/{cpf}", cpf)
                .exchangeToMono(response -> {
                    // 200 OK: Retorno esperado da API
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(Map.class);
                    }

                    // 404: Pode ser CPF Inválido (JSON) ou Erro de Infra (HTML)
                    if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        boolean isHtml = response.headers().contentType()
                                .map(mediaType -> mediaType.includes(MediaType.TEXT_HTML))
                                .orElse(false);

                        if (isHtml) {
                            // Se for HTML, tratamos como erro técnico para cair no fallback de Mock
                            return Mono.error(new RuntimeException("API externa retornou HTML de erro (404 Infra)"));
                        }

                        // Se for JSON, é um 404 de negócio (CPF não existe na base deles)
                        return Mono.error(new BusinessException(
                                "CPF inválido para o associado",
                                HttpStatus.NOT_FOUND,
                                Map.of("associadoID", "CPF inválido: " + cpf)
                        ));
                    }

                    // Outros erros (500, 503, etc)
                    return Mono.error(new RuntimeException("Erro na API externa: Status " + response.statusCode()));
                })
                .map(body -> "ABLE_TO_VOTE".equalsIgnoreCase((String) body.get("status")))
                .block(); // método não reativo (síncrono)
    }

    private boolean validarViaMock(String associadoCPF) {
        log.info("Validação de CPF: mock.");

        // Lógica: Se o último dígito for par, pode votar (ABLE_TO_VOTE)
        // Se for ímpar, não pode (UNABLE_TO_VOTE)
        int ultimoDigito = Character.getNumericValue(associadoCPF.charAt(10));
        return ultimoDigito % 2 == 0;
    }
}
