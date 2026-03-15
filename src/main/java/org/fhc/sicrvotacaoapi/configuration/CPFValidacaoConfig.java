package org.fhc.sicrvotacaoapi.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class CPFValidacaoConfig {

    @Value("${external-services.cpf-validator.base-url}")
    private String baseUrl;

    @Value("${external-services.cpf-validator.path-url}")
    private String pathUrl;

    @Value("${external-services.cpf-validator.timeout}")
    private Duration timeout;

    @Value("${external-services.cpf-validator.enabled}")
    private boolean enabled;

    @Value("${external-services.cpf-validator.use-mock-on-failure}")
    private boolean useMockOnFailure;

    // Métodos Getter para a Service conseguir ler os valores
    public boolean isEnabled() { return enabled; }
    public boolean isUseMockOnFailure() { return useMockOnFailure; }

    @Bean
    public WebClient cpfValidatorWebClient() {

        HttpClient httpClient = HttpClient.create()
                .responseTimeout(timeout);

        return WebClient.builder()
                .baseUrl(baseUrl + pathUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
