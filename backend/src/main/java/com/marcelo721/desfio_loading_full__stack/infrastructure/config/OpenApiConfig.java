package com.marcelo721.desfio_loading_full__stack.infrastructure.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.actuate.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    public static final String BEARER_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Central de Solicitações API")
                        .version("1.0.0")
                        .description("API REST para registro e acompanhamento de solicitações internas. "
                                + "Fluxo de status obrigatório: OPEN -> IN_PROGRESS -> COMPLETED. "
                                + "Obtenha um token em /api/auth/login e informe-o no botão Authorize."))
                .components(new Components().addSecuritySchemes(BEARER_SCHEME,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
