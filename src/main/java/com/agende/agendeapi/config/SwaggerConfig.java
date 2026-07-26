package com.agende.agendeapi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .components(new Components()
                        .addSchemas("ErrorDTO", new Schema<>().$ref("#/components/schemas/ErrorDTO"))
                        .addSchemas("ResponseDTO", new Schema<>().$ref("#/components/schemas/ResponseDTO"))
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name("Authorization")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT"))
                        .addParameters("X-Tenant-ID",
                                new Parameter()
                                        .in("header")
                                        .required(false)
                                        .name("X-Tenant-ID")
                                        .description("Identificador do Tenant (schema do cliente)")
                                        .schema(new StringSchema())
                        )
                )

                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))

                .info(new Info()
                        .title("Agende API")
                        .version("1.0.0"));
    }
}
