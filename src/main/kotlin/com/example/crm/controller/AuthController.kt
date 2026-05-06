package com.example.crm.controller

import com.example.crm.exception.GoogleAuthenticationException
import com.example.crm.infrastructure.security.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody as OasRequestBody
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

data class AuthRequest(
    @field:Schema(example = "admin@saas.com") val email: String,
    @field:Schema(example = "string") val password: String
)

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/token")
    @Operation(
        summary = "Authenticate and return JWT token",
        requestBody = OasRequestBody(
            content = [Content(mediaType = "application/json",
                examples = [ExampleObject(value = "{\"email\": \"admin@saas.com\", \"password\": \"string\"}")])]
        )
    )
    fun token(@RequestBody req: AuthRequest): ResponseEntity<AuthResponse> {
        val token = authService.authenticateWithEmailPassword(req.email, req.password)
            ?: return ResponseEntity.status(401).build()
        return ResponseEntity.ok(AuthResponse(token))
    }

    @PostMapping("/google")
    @Operation(
        summary = "Authenticate with Google ID token and return JWT token",
        requestBody = OasRequestBody(
            content = [Content(mediaType = "application/json",
                examples = [ExampleObject(value = "{\"credential\": \"google_id_token\"}")])]
        )
    )
    fun google(@RequestBody req: GoogleAuthRequest): ResponseEntity<AuthResponse> {
        return try {
            val token = authService.authenticateWithGoogle(req.credential)
            ResponseEntity.ok(AuthResponse(token))
        } catch (_: GoogleAuthenticationException) {
            ResponseEntity.status(401).build()
        }
    }
}
