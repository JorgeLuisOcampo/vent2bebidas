package com.uniquindio.vent2bebidas.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Configura la documentación de Swagger y el botón "Authorize".
// El esquema "bearer-jwt" hace que Swagger envíe el encabezado
// Authorization: Bearer <token> en cada petición después de autorizarse.
@Configuration
public class OpenApiConfig {

    private static final String ESQUEMA = "bearer-jwt";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Vent2Bebidas")
                        .version("2.0")
                        .description("Marketplace de bebidas con envases retornables. "
                                + "1) Inicie sesión en /api/auth/login, 2) copie el token, "
                                + "3) péguelo en el botón Authorize."))
                .components(new Components().addSecuritySchemes(ESQUEMA,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(ESQUEMA));
    }
}
