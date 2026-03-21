package com.example.crm.infrastructure.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class SecurityConfig(private val jwtAuthenticationFilter: JwtAuthenticationFilter) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/api/v1/auth/**", "/actuator/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                    .anyRequest().authenticated()
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            // remove httpBasic so Swagger UI won't prompt for basic auth
            .httpBasic { it.disable() }

        return http.build()
    }
}
