package com.ustore.teammanagement.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig implements OpenApiConstants {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", criarSecurityScheme()))
                .info(criarApiInfo())
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                ;
    }

    private SecurityScheme criarSecurityScheme() {
        return new SecurityScheme()
                .type(Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(In.HEADER);
    }


    private Info criarApiInfo() {
        return new Info()
                .title(TITULO_API)
                .version(VERSAO_API)
                .description(DESCRICAO_API)
                .termsOfService(TERMOS_SERVICO);
    }

}