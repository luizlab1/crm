package com.example.crm.infrastructure.web.config

import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * Serve a small customization script into the Swagger UI static assets.
 */
@Configuration
class SwaggerUiCustomization : WebMvcConfigurer {
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        // Serve our custom JS to be available under /swagger-ui/swagger-auth.js
        // Map to classpath:/static so the file we added at src/main/resources/static
        // is served by Spring's resource handling.
        registry
            .addResourceHandler("/swagger-ui/swagger-auth.js")
            .addResourceLocations("classpath:/static/")
    }

    override fun addViewControllers(registry: ViewControllerRegistry) {
        // no-op
    }
}
