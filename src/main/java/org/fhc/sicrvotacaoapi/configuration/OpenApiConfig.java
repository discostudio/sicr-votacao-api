package org.fhc.sicrvotacaoapi.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.Schema;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenApiCustomizer globalResponsesCustomizer() {
        return openApi -> openApi.getPaths().values().forEach(pathItem ->
                pathItem.readOperations().forEach(operation -> {

                    operation.getResponses().addApiResponse("500",
                            new ApiResponse()
                                    .description("Erro interno do servidor")
                                    .content(new Content()
                                            .addMediaType("application/json",
                                                    new io.swagger.v3.oas.models.media.MediaType()
                                                            .schema(new Schema<>().$ref("#/components/schemas/ErrorResponseDTO"))
                                                            .example("""
                                                                {
                                                                    "message": "Erro interno do servidor",
                                                                    "fieldErrors": {}
                                                                }
                                                                """)
                                            )
                                    ));

                    operation.getResponses().addApiResponse("409",
                            new ApiResponse()
                                    .description("Conflito de estado da aplicação - ex.: unique constraint banco de dados")
                                    .content(new Content()
                                            .addMediaType("application/json",
                                                    new io.swagger.v3.oas.models.media.MediaType()
                                                            .schema(new Schema<>().$ref("#/components/schemas/ErrorResponseDTO"))
                                                            .example("""
                                                                {
                                                                    "message": "Erro ao armazenar informação",
                                                                    "fieldErrors": {
                                                                        "erro": "Operação não pôde ser concluída devido a conflito de dados"
                                                                    }
                                                                }
                                                                """)
                                            )
                                    ));

                })
        );
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de votação das assembléias")
                        .version("1.0")
                        .description("API responsável pelo escopo das votações em assembléias, incluindo pautas, sessões de votação, votos e resultado."));
    }
}
