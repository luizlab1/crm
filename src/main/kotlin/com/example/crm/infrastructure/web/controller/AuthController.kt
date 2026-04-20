package com.example.crm.infrastructure.web.controller

import com.example.crm.infrastructure.security.JwtService
import com.example.crm.application.port.input.UserUseCase
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.parameters.RequestBody as OasRequestBody
import io.swagger.v3.oas.annotations.media.Content as OasContent
import io.swagger.v3.oas.annotations.media.ExampleObject as OasExampleObject
import io.swagger.v3.oas.annotations.media.Schema

data class AuthRequest(
    @field:Schema(example = "admin@saas.com")
    val email: String,
    @field:Schema(example = "string")
    val password: String
)
data class AuthResponse(val token: String)

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(private val jwtService: JwtService, private val userUseCase: UserUseCase) {

    @PostMapping("/token")
    @Operation(
        summary = "Authenticate and return JWT token",
        requestBody = OasRequestBody(
            content = [OasContent(mediaType = "application/json", examples = [OasExampleObject(value = "{\"email\": \"admin@saas.com\", \"password\": \"string\"}")])]
        )
    )
    fun token(@org.springframework.web.bind.annotation.RequestBody req: AuthRequest): ResponseEntity<AuthResponse> {
        val user = userUseCase.getByEmail(req.email) ?: return ResponseEntity.status(401).build()

        // Verify password using BCryptPasswordEncoder. Some bcrypt implementations
        // produce $2b$ prefixes; normalize to $2a$ for compatibility with older libs.
        val stored = user.passwordHash.replaceFirst("^\\$2b\\$".toRegex(), "\$2a\$")
        val encoder = BCryptPasswordEncoder()
        if (!encoder.matches(req.password, stored)) {
            return ResponseEntity.status(401).build()
        }

        val token = jwtService.generateToken(
            user.email,
            mapOf("email" to user.email, "userId" to user.id, "tenantId" to user.tenantId)
        )
        return ResponseEntity.ok(AuthResponse(token))
    }
}
