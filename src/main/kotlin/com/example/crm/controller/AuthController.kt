package com.example.crm.controller

import com.example.crm.infrastructure.security.JwtService
import com.example.crm.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody as OasRequestBody
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*

data class AuthRequest(
    @field:Schema(example = "admin@saas.com") val email: String,
    @field:Schema(example = "string") val password: String
)

data class AuthResponse(val token: String)

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val jwtService: JwtService,
    private val userService: UserService
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
        val user = userService.getByEmail(req.email)
        val response = if (user != null) {
            val stored = user.passwordHash.replaceFirst("^\\$2b\\$".toRegex(), "\$2a\$")
            val matches = BCryptPasswordEncoder().matches(req.password, stored)
            if (matches) {
                val token = jwtService.generateToken(
                    user.email,
                    mapOf("email" to user.email, "userId" to user.id, "tenantId" to user.tenantId)
                )
                ResponseEntity.ok(AuthResponse(token))
            } else {
                ResponseEntity.status(401).build()
            }
        } else {
            ResponseEntity.status(401).build()
        }
        return response
    }
}
