package com.example.crm.infrastructure.web.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.parameters.RequestBody
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.Schema
import org.springdoc.core.customizers.OpenApiCustomiser
import org.springframework.context.annotation.Bean
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

    @Bean
    fun authRequestExampleCustomizer(): OpenApiCustomiser = OpenApiCustomiser { openApi ->
        try {
            val path = openApi.paths?.get("/api/v1/auth/token") ?: return@OpenApiCustomiser
            val post = path.post ?: return@OpenApiCustomiser

            val rb: RequestBody = post.requestBody ?: RequestBody()
            val content: Content = rb.content ?: Content()
            val media: MediaType = content.get("application/json") ?: MediaType()

            val schema: Schema<Any> = media.schema as? Schema<Any> ?: Schema()
            // set the example that will appear in Swagger UI request body
            schema.example = mapOf("email" to "admin@saas.com", "password" to "string")
            media.schema = schema

            content.addMediaType("application/json", media)
            rb.content = content
            post.requestBody = rb
        } catch (_: Exception) {
            // non-fatal; if OpenAPI structure differs we skip
        }
    }
}
