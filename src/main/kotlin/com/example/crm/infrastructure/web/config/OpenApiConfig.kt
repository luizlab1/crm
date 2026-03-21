package com.example.crm.infrastructure.web.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.Components
import org.springframework.context.annotation.Bean
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.security.SecurityRequirement
// Bean already imported above
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        // Add a bearer authentication scheme so Swagger UI knows about the security scheme
        val bearerScheme = SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")

        return OpenAPI()
            .components(Components().addSecuritySchemes("bearerAuth", bearerScheme))
            .addSecurityItem(SecurityRequirement().addList("bearerAuth"))
            .info(
                Info()
                    .title("CRM API")
                    .version("v1")
                    .description("Swagger UI for CRM API")
            )
    }

    // Keep OpenAPI bean minimal; adding request examples dynamically can be brittle
    // across springdoc versions. We add the example via the static swagger-auth.js
    // script which pre-fills the login form client-side.
}
